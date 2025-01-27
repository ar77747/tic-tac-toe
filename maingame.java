import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

public class maingame extends JFrame{
    BufferedImage cross,circle,diag1,diag2;
    JLabel dw;
    Random random;
    boolean mainturn;
    boolean turn;
    boolean aiturn,airun,aistart;
    int grid[][];
    JComboBox<String> cb1;
    JLabel status;
    JLabel gridimg[][];
    int fixx=70;
    int fixy=100;
    int count=0;
    int hc[]={160,345,525};
    int vc[]={135,315,495};
    JLabel horizontalLine,verticalLine;
    public maingame() {
        try {
            diag1=ImageIO.read(new File("diag1.png"));
            diag2=ImageIO.read(new File("diag2.png"));
            circle=ImageIO.read(new File("circle.png"));
            cross=ImageIO.read(new File("cross.png"));
            diag1=resize(diag1,500,500);
            diag2=resize(diag2,500,500);
            circle=resize(circle,160,160);
            cross=resize(cross,160,160);
        } catch (Exception e) {
            System.err.println(e);
        }
        aistart=false;
        dw=new JLabel();
        random=new Random();
        horizontalLine=new JLabel();
        verticalLine=new JLabel();
        horizontalLine.setBounds(0,0,0,0);
        horizontalLine.setOpaque(true);
        horizontalLine.setBackground(Color.GREEN);
        verticalLine.setBounds(0,0,0,0);
        verticalLine.setOpaque(true);
        verticalLine.setBackground(Color.GREEN);
        turn=true;
        mainturn=true;
        aiturn=false;
        grid=new int[3][3];
        gridimg=new JLabel[3][3];
        String[] modes={"PVP","VS PC"};
        cb1=new JComboBox<>(modes);
        status=new JLabel("Select Game Mode");
        status.setForeground(Color.WHITE);
        status.setFont(new Font("Arial",Font.PLAIN,24));
        status.setBounds(150,0,250,40);
        cb1.setBounds(400,0,150,40);
        for(int x=0;x<3;x++) {
            for(int y=0;y<3;y++) {
                gridimg[x][y]=new JLabel();
                gridimg[x][y].setOpaque(true);
                gridimg[x][y].setBounds((x*180)+60,(y*180)+90,160,160);
                gridimg[x][y].setBackground(Color.WHITE);
                final int rx=x;
                final int ry=y;
                gridimg[rx][ry].addMouseListener(new MouseAdapter() {public void mousePressed(MouseEvent e) {place(rx,ry);}});     
            }
        }
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        add(status);
        add(cb1);
        add(horizontalLine);
        add(verticalLine);
        add(dw);
        for(int x=0;x<3;x++) {
            for(int y=0;y<3;y++) {
                add(gridimg[x][y]);
            }
        }
        setVisible(true);
        Insets is=getInsets();
        setSize(640+is.left+is.right,680+is.top+is.bottom);
        setLocationRelativeTo(null);
        getContentPane().setBackground(new Color(0,0,0));
        cb1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int tmp=cb1.getSelectedIndex();
                if(tmp==0) {
                    aistart=false;
                    newgame();
                    airun=false;
                } else {
                    newgame();
                    airun=true;
                    aiturn=true;
                }
            }
        });
    }
    void newgame() {
        count=0;
        horizontalLine.setBounds(0,0,0,0);
        verticalLine.setBounds(0,0,0,0);
        dw.setBounds(0,0,0,0);
        for(int x=0;x<3;x++) {
            for(int y=0;y<3;y++) {
                grid[x][y]=0;
                gridimg[x][y].setIcon(null);
                gridimg[x][y].setBackground(Color.WHITE);
            }
        }
        if(mainturn) {
            turn=false;
            mainturn=false;
        } else {
            mainturn=true;
            turn=true;
        }
        if(aistart&&airun) {
            aistart=false;
            while (true) { 
                int tx=random.nextInt(3);
                int ty=random.nextInt(3);
                if(grid[tx][ty]==0) {
                    aiturn=false;
                    place(tx,ty);
                    break;
                }
            }            
        } else aistart=true;
    }
    void place(int x,int y) {
        if(grid[x][y]==0) {
            if(turn) {
                gridimg[x][y].setIcon(new ImageIcon(circle));
                grid[x][y]=1;
                turn=false;
            } else {
                gridimg[x][y].setIcon(new ImageIcon(cross));
                grid[x][y]=2;
                turn=true;
            }
            count++;
            int rf=row_won();
            if(rf!=-1) {
                if(rf==1) JOptionPane.showMessageDialog(this, "O Wins!");
                else JOptionPane.showMessageDialog(this, "X Wins!");
                newgame();
                return;
            }
            int cf=col_won();
            if(cf!=-1) {
                if(cf==1) JOptionPane.showMessageDialog(this, "O Wins!");
                else JOptionPane.showMessageDialog(this, "X Wins!");
                newgame();
                return;
            }
            int df=diag_won();
            if(df!=-1) {
                if(df==1) JOptionPane.showMessageDialog(this, "O Wins!");
                else JOptionPane.showMessageDialog(this, "X Wins!"); 
                newgame();
                return;           
            }
            if(count==9) {
                JOptionPane.showMessageDialog(this, "Nobody Wins!");
                newgame();
                return;
            }
            if(airun) {
                if(aiturn) {
                    while (true) { 
                        int tx=random.nextInt(3);
                        int ty=random.nextInt(3);
                        if(grid[tx][ty]==0) {
                            aiturn=false;
                            place(tx,ty);
                            break;
                        }
                    }
                } else {
                    aiturn=true;
                }
            }
        }
    }
    BufferedImage resize(BufferedImage img, int newW, int newH) { 
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return dimg;
    }  
    int row_won()
    {
        int won=-1;
        for(int y=0;y<3;y++) {
            if(grid[0][y]==1&&grid[1][y]==1&&grid[2][y]==1) {
                horizontalLine.setBounds(fixx,hc[y],500,10);
                won=1;
                break;
            } else if(grid[0][y]==2&&grid[1][y]==2&&grid[2][y]==2) {
                horizontalLine.setBounds(fixx,hc[y],500,10);
                won=2;
                break;
            }
        }
        return won;
    }
    int col_won()
    {
        int won=-1;
        for(int x=0;x<3;x++) {
            if(grid[x][0]==1&&grid[x][1]==1&&grid[x][2]==1) {
                verticalLine.setBounds(vc[x],fixy,10,500);
                won=1;
                break;
            } else if(grid[x][0]==2&&grid[x][1]==2&&grid[x][2]==2) {
                verticalLine.setBounds(vc[x],fixy,10,500);
                won=2;
                break;
            }
        }
        return won;
    }
    int diag_won()
    {
        int won=-1;
        if(grid[0][0]==1&&grid[1][1]==1&&grid[2][2]==1) {
            dw.setIcon(new ImageIcon(diag1));
            dw.setBounds(70,100,500,500);
            won=1;
        } else if(grid[0][0]==2&&grid[1][1]==2&&grid[2][2]==2) {
            dw.setIcon(new ImageIcon(diag1));
            dw.setBounds(70,100,500,500);
            won=2;
        } else if(grid[0][2]==1&&grid[1][1]==1&&grid[2][0]==1) {
            dw.setIcon(new ImageIcon(diag2));
            dw.setBounds(70,100,500,500);
            won=1;      
        } else if(grid[0][2]==2&&grid[1][1]==2&&grid[2][0]==2) {
            dw.setIcon(new ImageIcon(diag2));
            dw.setBounds(70,100,500,500);
            won=2;      
        }
        return won;
    }
    public static void main(String args[]) {
        maingame m1=new maingame();
    }
}