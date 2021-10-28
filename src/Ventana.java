import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.Objects;

// este proyecto está siendo desarrollado con JDK 17
public class Ventana extends JFrame {
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();// se obtienen las dimensiones de la pantalla
    private JLabel logo = new JLabel();
    private JLabel texto = new JLabel();
    private JLabel imagen = new JLabel();
    private JPanel panel = new JPanel(null);
    private String codigo = "";
    private  final String url = "jdbc:mysql://localhost:3306/verificador_de_precios";
    private  final String user = "root";
    private  final String pwd = "root";

    public Ventana(){
        //setup de la ventana
        this.setTitle("Verificador de precios");
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setUndecorated(true);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // añade el key listener para input del código del producto al componente Jframe
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    getProducto(codigo);
                    codigo = "";
                }
                else codigo += e.getKeyChar();
            }
        });
        // Se llama al método para crear la GUI
        this.setUpUI();

        this.add(panel);
        this.setVisible(true);
    }

    public void setUpUI(){
        // En este método se hace todo lo correspondiente con la GUI
        panel.setBackground(Color.GREEN);

        // setup del logo
        logo.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("img/no-logo.jpg"))));
        logo.setSize(474,280);
        logo.setLocation((screenSize.width/2) - (logo.getWidth()/2) , 0);
        panel.add(logo);

        // setup del texto y de la informacion del producto
        texto.setText("Pase el código del artículo por el escáner");
        texto.setSize(510, 50);
        texto.setFont(new Font("Comic Sans MS", Font.PLAIN, 26));
        texto.setForeground(Color.WHITE);
        texto.setLocation((screenSize.width/2) - (texto.getWidth()/2), (screenSize.height/2) - 100);
        panel.add(texto);


        // setup de la imagen del producto y codigo de barras
        imagen.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource("img/barcode-scan.gif"))));
        imagen.setSize(498, 498);
        imagen.setLocation((screenSize.width/2) - (imagen.getWidth()/2) ,screenSize.height /2);
        panel.add(imagen);

        panel.paintImmediately(panel.getVisibleRect());
    }

    public void getProducto(String codigo){
        try {
            ResultSet resultSet;
            String query =
                    "SELECT producto_nombre, " +
                    "producto_precio, " +
                    "producto_cantidad, " +
                    "producto_imagen " +
                    "FROM productos WHERE producto_codigo = " + codigo ;
            //Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, user, pwd);
            PreparedStatement consulta = connection.prepareStatement(query);
            resultSet = consulta.executeQuery();

            if (resultSet.next()) {
                DisplayProducto(resultSet);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se puede conectar a la base de datos o no existe ese articulo, en fin la hipotenusa.");
        }
    }

    public void DisplayProducto(ResultSet resultSet) {
        //cambia la informacion de los labels con los resultados de la consulta del producto
            try {
                    System.out.println("llego display producto");
                    texto.setText(null);
                    texto.setLocation((screenSize.width / 4), (screenSize.height / 2) - 100);
                    texto.setSize(600, 450);
                    texto.setText(String.format("Nombre del producto: %s \n Precio del producto: %s \n Cantidad en stock: %s",
                            resultSet.getString(1),
                            resultSet.getString(2),
                            resultSet.getString(3)));

                    imagen.setIcon(null);
                    ImageIcon icon = new ImageIcon(new URL(resultSet.getString(4)));
                    imagen.setIcon(new ImageIcon(icon.getImage().getScaledInstance(250,250, Image.SCALE_DEFAULT)));

                    panel.paintImmediately(panel.getVisibleRect());

                    Thread.sleep(800);
                    this.setUpUI();
            } catch (Exception throwables) {
                throwables.printStackTrace();
                JOptionPane.showMessageDialog(this, "No se puede mostrar el artículo, en fin la hipotenusa.");
            }

    }

}
