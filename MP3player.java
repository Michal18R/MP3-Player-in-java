/**
 * Created by Hedi on 2018-12-31.
 */

    import javazoom.jl.decoder.JavaLayerException;
    import javazoom.jl.player.Player;

    import javax.swing.*;
    import javax.swing.filechooser.FileNameExtensionFilter;
    import java.awt.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;
    import java.io.*;
    import java.nio.file.Files;
    import java.nio.file.Paths;


public class MP3player implements ActionListener{

    //tworzę pole wyświetlania tytułu utworu
    JLabel song = new JLabel();

    //tworzę przyciski
    JButton startButton = new JButton("Play");
    JButton choseButton = new JButton("Choose song");
    JFileChooser fileChooser;
    InputStream inputStream;
    BufferedInputStream bufferedInputStream;
    File myFile=null;
    String filename;
    String filePath;
    long totalLength;
    long pause;
    Player player;
    Thread playThread;
    Thread resumeThread;

    MP3player(){
        prepareGUI();
        addActionEvents();

    }

    public void prepareGUI(){

        //tworzę ogólne okienko programu
        JFrame frame = new JFrame();
        //tytuł
        frame.setTitle("MP3 PLAYER");
        frame.getContentPane().setLayout(null);
        //kolor tła
        frame.getContentPane().setBackground(Color.blue);
        //rozmiar okna
        frame.setSize(520,400);
        //centrowanie okna programu
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //ustalanie rozmiarów i umiejscowienie przycisków wzgłedem okienka programu
        startButton.setLayout(null);
        startButton.setBounds(200,150,100,60);
        frame.add(startButton);

        choseButton.setLayout(null);
        choseButton.setBounds(190,250,120,60);
        frame.add(choseButton);

        song.setLayout(null);
        song.setBounds(65,50,390,60);
        song.setFont(new Font("Arial", Font.PLAIN, 30));
        frame.add(song);


    }

        public void addActionEvents(){
            //rejestrowanie listeneró do przycisków
            choseButton.addActionListener(this);
            startButton.addActionListener(this);


        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource()== choseButton){
                //kod dla okienka wyboru piosenki
                fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File("C:\\Users\\Hedi\\Desktop\\songs mp3 player"));
                fileChooser.setDialogTitle("Choose song");
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fileChooser.setFileFilter(new FileNameExtensionFilter("Mp3 files","mp3"));
                if(fileChooser.showOpenDialog(choseButton)==JFileChooser.APPROVE_OPTION){
                    myFile=fileChooser.getSelectedFile();
                    filename=fileChooser.getSelectedFile().getName();
                    filePath=fileChooser.getSelectedFile().getPath();
                }
                pause = 0;
                startButton.setText("Play");
            }
            if(e.getSource()== startButton){
               if(playThread != null && playThread.isAlive()){
                   try {
                       pauseMusic();
                       startButton.setText("Resume");
                   } catch (IOException e1) {
                       e1.printStackTrace();
                   }
               } else if(pause == 0){
                   try {
                       startMusic(filePath, 0);
                       if(filename==null || filename.isEmpty()) {
                           song.setText("Please select your song!");
                       } else {
                           song.setText("You listening: "+filename);
                           startButton.setText("Pause");

                       }
                   } catch (IOException e1) {
                       e1.printStackTrace();
                   } catch (JavaLayerException e1) {
                       e1.printStackTrace();
                   }
               } else {
                   try {
                       resumeMusic();
                       startButton.setText("Pause");
                   } catch (IOException e1) {
                       e1.printStackTrace();
                   } catch (JavaLayerException e1) {
                       e1.printStackTrace();
                   }
               }
            }

        }


    private void startMusic(String filename, long from) throws IOException, JavaLayerException {

        if(filename==null || filename.isEmpty()) {
            return;
        }

        inputStream = Files.newInputStream(Paths.get(filename));
        if(from == 0 ) {
            totalLength = inputStream.available(); // sprawdza ile jest bajtów na początku
        } else {
            inputStream.skip(totalLength - from);
        }
        player = new Player(inputStream);


        playThread = new Thread(() -> {
            try {
                player.play();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        });
        playThread.start();

    }

    private void pauseMusic() throws IOException {
        if(playThread!=null && playThread.isAlive()) {
            pause = inputStream.available(); // sprawdzanie ile bajtów zostało
            player.close();
        }
    }

    private void resumeMusic() throws IOException, JavaLayerException {
        startMusic(filePath, pause);
    }

    public static void main(String...args){
        new MP3player();
    }
}