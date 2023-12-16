import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.stream.IntStream;

public class Project2_TerrainGenerator{
    static int WIDTH = 500; //width of the image
    static int HEIGHT = 500; //height of the image
    static BufferedImage Display; //the image we are displaying
    static JFrame window; //the frame containing our window
    static Graphics2D g2d;

    public static void main(String[] args) {
        //run the GUI on the special event dispatch thread (EDT)
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                //Create the window and set options
                //The window
                window = new JFrame("RandomWalker");
                window.setPreferredSize(new Dimension(WIDTH+100,HEIGHT+50));
                window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                window.setVisible(true);
                window.pack();


                //Display panel/image
                JPanel DisplayPanel = new JPanel();
                Display = new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_ARGB);
                DisplayPanel.add(new JLabel(new ImageIcon(Display)));
                window.add(DisplayPanel,BorderLayout.CENTER);

                //Config panel
                JPanel Configuration = new JPanel();
                Configuration.setBackground(new Color(230,230,230));
                Configuration.setPreferredSize(new Dimension(100,500));
                Configuration.setLayout(new FlowLayout());

                //Step count input
                JLabel StepCountLabel = new JLabel("Step Count:");
                Configuration.add(StepCountLabel);

                JTextField StepCount = new JTextField("500");
                StepCount.setPreferredSize(new Dimension(100,25));
                Configuration.add(StepCount);

                //Walker type input
                JLabel WalkerType = new JLabel("Walker Type:");
                Configuration.add(WalkerType);
                ButtonGroup WalkerTypes = new ButtonGroup();
                JRadioButton Standard = new JRadioButton("Standard");
                Standard.setActionCommand("standard");
                Standard.setSelected(true);
                JRadioButton Picky = new JRadioButton("Picky");
                Picky.setActionCommand("picky");
                WalkerTypes.add(Standard);
                WalkerTypes.add(Picky);
                Configuration.add(Standard);
                Configuration.add(Picky);

                //Walker type input
                JLabel Geometry = new JLabel("World Geometry:");
                Configuration.add(Geometry);
                ButtonGroup Geometries = new ButtonGroup();
                JRadioButton Bounded = new JRadioButton("Bounded");
                Bounded.setActionCommand("bounded");
                Bounded.setSelected(true);
                JRadioButton Toroidal = new JRadioButton("Toroidal");
                Toroidal.setActionCommand("toroidal");
                Geometries.add(Bounded);
                Geometries.add(Toroidal);
                Configuration.add(Bounded);
                Configuration.add(Toroidal);

                //Rendering Style input
                JLabel Terrain = new JLabel("Render Style");
                Configuration.add(Terrain);
                ButtonGroup TerrainTypes = new ButtonGroup();
                JRadioButton Satellite = new JRadioButton("Satellite");
                Satellite.setActionCommand("satellite");
                Satellite.setSelected(true);
                JRadioButton Height = new JRadioButton("Height");
                Height.setActionCommand("height");
                Height.setSelected(true);
                TerrainTypes.add(Satellite);
                TerrainTypes.add(Height);
                Configuration.add(Satellite);
                Configuration.add(Height);


                //Dimensions
                JLabel Dimensions = new JLabel("Dimensions");
                Configuration.add(Dimensions);
                JTextField dimensions = new JTextField("200");
                dimensions.setPreferredSize(new Dimension(100, 25));
                Configuration.add(dimensions);


                //Run Button
                JButton Run = new JButton("Walk");
                Run.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        UpdateDisplay(Display);
                        int count = Integer.parseInt(StepCount.getText());
                        String walk_type = WalkerTypes.getSelection().getActionCommand();
                        String geom_type = Geometries.getSelection().getActionCommand();
                        String render_type = TerrainTypes.getSelection().getActionCommand();
                        int dim = Integer.parseInt(dimensions.getText());

                        int randX = 0;
                        int randY = 0;
                        Random rand = new Random();

                        float terrainArray[][] = new float[dim][dim];
                        float newTerrainArr[][] = new float[][]{};
                        int x = terrainArray.length/2-1;
                        int y = terrainArray[x].length/2-1;


                        if(walk_type == "standard"){
                            for(int i = 0; i<count; i++){
                                randX = rand.nextInt( 2-(-1))-1;
                                randY = rand.nextInt( 2-(-1))-1;
                                x = x + randX;
                                y = y + randY;

                                if(geom_type == "toroidal"){
                                    //Toroidal
                                    if(x >= WIDTH ){
                                        x = 0;
                                    }
                                    if(x < 0 ){
                                        x = WIDTH-1;
                                    }
                                    if(y >= HEIGHT ){
                                        y = 0;
                                    }
                                    if(y<0 ){
                                        y = HEIGHT-1;
                                    }
                                }

                                if(geom_type == "bounded"){
                                    BoundX(x, randX);
                                    BoundY(y, randY);
                                }

                                //need to make sure every spot is in the range of the array
                                if(inBounds(x-1, y-1, terrainArray.length))
                                    terrainArray[x-1][y-1] += 1.0f;

                                if(inBounds(x, y-1, terrainArray.length))
                                    terrainArray[x][y-1] += 1.0f;

                                if(inBounds(x+1, y-1, terrainArray.length))
                                    terrainArray[x+1][y-1] += 1.0f;

                                if(inBounds(x-1, y, terrainArray.length))
                                    terrainArray[x-1][y] += 1.0f;

                                if(inBounds(x, y, terrainArray.length))
                                    terrainArray[x][y] += 1.0f;

                                if(inBounds(x+1, y, terrainArray.length))
                                    terrainArray[x+1][y] += 1.0f;

                                if(inBounds(x-1, y, terrainArray.length))
                                    terrainArray[x-1][y] += 1.0f;

                                if(inBounds(x, y+1, terrainArray.length))
                                    terrainArray[x][y+1] += 1.0f;

                                if(inBounds(x+1, y+1, terrainArray.length))
                                    terrainArray[x+1][y+1] += 1.0f;

                            }
                            if(render_type == "satellite")
                                TerrainImage(terrainArray, Display);
                            //height map
                            for(int j = 0; j < terrainArray.length; j++)
                                newTerrainArr = terrainArray;
                        }

                        if(render_type == "height")
                            DrawHeight(newTerrainArr);


                        if(walk_type == "picky"){
                            Random rand2 = new Random();
                            int randSteps = 0;
                            for(int i = 0; i<count; i++){
                                //picks random direction
                                randX = rand.nextInt( 2-(-1))-1;
                                randY = rand.nextInt( 2-(-1))-1;
                                x = x + randX;
                                y = y + randY;

                                //pick random # of steps
                                randSteps = rand2.nextInt(11-1) + 1;
                                for(int j = 0; j<randSteps; j++){
                                    x = x + randX;
                                    y = y + randY;
                                    if(geom_type == "toroidal"){
                                        if(x >= WIDTH ){
                                            x = 0;
                                        }
                                        if(x < 0 ){
                                            x = WIDTH-1;
                                        }
                                        if(y >= HEIGHT ){
                                            y = 0;
                                        }
                                        if(y<0 ){
                                            y = HEIGHT-1;
                                        }

                                    }

                                    if(geom_type == "bounded"){
                                        BoundX(x, randX);
                                        BoundY(y, randY);
                                    }

                                    if(inBounds(x-1, y-1, terrainArray.length))
                                        terrainArray[x-1][y-1] += 1.0f;

                                    if(inBounds(x, y-1, terrainArray.length))
                                        terrainArray[x][y-1] += 1.0f;

                                    if(inBounds(x+1, y-1, terrainArray.length))
                                        terrainArray[x+1][y-1] += 1.0f;

                                    if(inBounds(x-1, y, terrainArray.length))
                                        terrainArray[x-1][y] += 1.0f;

                                    if(inBounds(x, y, terrainArray.length))
                                        terrainArray[x][y] += 1.0f;

                                    if(inBounds(x+1, y, terrainArray.length))
                                        terrainArray[x+1][y] += 1.0f;

                                    if(inBounds(x-1, y, terrainArray.length))
                                        terrainArray[x-1][y] += 1.0f;

                                    if(inBounds(x, y+1, terrainArray.length))
                                        terrainArray[x][y+1] += 1.0f;

                                    if(inBounds(x+1, y+1, terrainArray.length))
                                        terrainArray[x+1][y+1] += 1.0f;

                                }
                                TerrainImage(terrainArray, Display);
                                //height map
                            }
                        }
                        window.repaint();
                    }
                });

                Configuration.add(Run);
                window.add(Configuration,BorderLayout.EAST);

            }
        });
    }

    //A method to update the display image to match one generated by you
    static void UpdateDisplay(BufferedImage img){
        //Below 4 lines draws the input image on the display image
        Graphics2D g = (Graphics2D) Display.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0,0,WIDTH,HEIGHT);
        g.drawImage(img,0,0,null);

        //forces the window to redraw its components (i.e., the image)
        window.repaint();
    }


    static void TerrainImage(float arr[][], BufferedImage Display){

        //need array, bufferegImage, return image
        Graphics2D g2d = (Graphics2D) Display.getGraphics();
        float max = 0;

        for(int i = 0; i<arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j] > max)
                    max = arr[i][j];
                // System.out.println("in");
            }
        }
        for(int i = 0; i<arr.length-1; i++) {
            for (int j = 0; j < arr[i].length - 1; j++) {
                if(arr[i][j] >= 0.75*max){
                    g2d.setColor(new Color(230, 230, 255));
                }
                else if(arr[i][j] < 0.75*max && arr[i][j]  >= 0.5*max){
                    g2d.setColor(new Color(100, 75, 50));
                }
                else if(arr[i][j] < 0.5*max && arr[i][j]  >= 0.1*max){
                    g2d.setColor(new Color(30, 150, 30));
                }
                else if(arr[i][j] >= 0.1*max && arr[i][j]  >= 0.05*max){
                    g2d.setColor(new Color(255, 255, 195));
                }else{
                    g2d.setColor(new Color(27, 228, 255));
                }

                g2d.fillRect(i*(WIDTH/ arr.length), j * (HEIGHT/ arr.length), WIDTH, HEIGHT);
            }
        }
    }

    static Color[] HeightImage(float arr[][]){
        float max = 0;
        for(int i = 0; i<arr.length; i++) {
            for (int j = 0; j < arr[i].length; j++) {
                if (arr[i][j] > max)
                    max = arr[i][j];
            }
        }
        Color[] colorArray = new Color[(int) (max+1)];
        float  dBlack = 255/max;
        float sBlack = 0;

        for(int i = 0; i<(int)(max+1); i++){
            colorArray[i] = new Color((int) sBlack, (int) sBlack, (int) sBlack);
            sBlack += dBlack;
        }

        return colorArray;

    }
    static void DrawHeight(float arr[][]){
        Graphics2D g2d = (Graphics2D) Display.getGraphics();
        Color [] colorArray = HeightImage(arr);
        for(int i = 0; i<arr.length; i++){
            for(int j = 0; j<arr[i].length; j++){
                g2d.setColor(colorArray[(int)arr[i][j]]);
                g2d.fillRect(i*(WIDTH/ arr.length), j * (HEIGHT/ arr.length), WIDTH, HEIGHT);

            }
        }
    }

    static int BoundX(int x, int randX){
        if(x >= WIDTH || x < 0 ){
            x -= randX;
        }

        return x;

    }
    static boolean inBounds(int x, int y, int dim){
        if(x > dim-1 || x < 0 || y > dim-1 || y < 0){
            return false;
        }
        return true;
    }
    static int BoundY(int y, int randY){
        if(y >= HEIGHT || y<0){
            y -= randY;
        }
        return y;
    }

    static int ToroidalX(int x){
        if(x >= WIDTH ){
            x = 0;
        }
        if(x < 0 ){
            x = WIDTH-1;
        }
        return x;
    }
    static int ToroidalY(int y){
        if(y >= HEIGHT ){
            y = 0;
        }
        if(y<0 ){
            y = HEIGHT-1;
        }
        return y;
    }

}
































