package test1;
 import java.applet.*; 
 import java.awt.*; 
 import java.awt.event.*; 
 import java.util.*; 
 import javax.swing.*; 
 import java.awt.geom.*; 
 import java.io.*; 

 class Point implements Serializable 
 { 
 int x,y; 
 Color col; 
 int tool; 
 int boarder; 

 Point(int x, int y, Color col, int tool, int boarder) 
 { 
 this.x = x; 
 this.y = y; 
 this.col = col; 
 this.tool = tool; 
 this.boarder = boarder; 
 } 
 } 

 class paintboard extends Frame implements ActionListener,MouseMotionListener,MouseListener,ItemListener 
 { 
 int x = -1, y = -1; 
 int con = 1;//画笔大小 
 int Econ = 5;//橡皮大小 

 int toolFlag = 0;//toolFlag:工具标记 
 //toolFlag工具对应表： 
 //（0--画笔）；（1--橡皮）；（2--清除）； 
 //（3--直线）；（4--圆）；（5--矩形）； 

 Color c = new Color(0,0,0); //画笔颜色 
 BasicStroke size = new BasicStroke(con,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL);//画笔粗细 
 Point cutflag = new Point(-1, -1, c, 6, con);//截断标志 

 Vector paintInfo = null;//点信息向量组 
 int n = 1; 

 FileInputStream picIn = null; 
 FileOutputStream picOut = null; 

 ObjectInputStream VIn = null; 
 ObjectOutputStream VOut = null; 

 // *工具面板--画笔，直线，圆，矩形，多边形,橡皮，清除*/ 
 Panel toolPanel; 
 Button eraser, drLine,drCircle,drRect; 
 Button clear ,pen; 
 Choice ColChoice,SizeChoice,EraserChoice; 
 Button colchooser; 
 Label 颜色,大小B,大小E; 
 //保存功能 
 Button openPic,savePic; 
 FileDialog openPicture,savePicture; 

 paintboard(String s) 
 { 
 super(s); 
 addMouseMotionListener(this); 
 addMouseListener(this); 

 paintInfo = new Vector(); 

 /*各工具按钮及选择项*/ 
 //颜色选择 
 ColChoice = new Choice(); 
 ColChoice.add("black"); 
 ColChoice.add("red"); 
 ColChoice.add("blue"); 
 ColChoice.add("green"); 
 ColChoice.addItemListener(this); 
 //画笔大小选择 
 SizeChoice = new Choice(); 
 SizeChoice.add("1"); 
 SizeChoice.add("3"); 
 SizeChoice.add("5"); 
 SizeChoice.add("7"); 
 SizeChoice.add("9"); 
 SizeChoice.addItemListener(this); 
 //橡皮大小选择 
 EraserChoice = new Choice(); 
 EraserChoice.add("5"); 
 EraserChoice.add("9"); 
 EraserChoice.add("13"); 
 EraserChoice.add("17"); 
 EraserChoice.addItemListener(this); 
 //
 toolPanel = new Panel(); 

 clear = new Button("清除clear"); 
 eraser = new Button("橡皮eraser"); 
 pen = new Button("画笔pen"); 
 drLine = new Button("画直线stright_line"); 
 drCircle = new Button("画圆形"); 
 drRect = new Button("画矩形"); 

 openPic = new Button("打开图画"); 
 savePic = new Button("保存图画"); 

 colchooser = new Button("显示调色板"); 

 //各组件事件监听 
 clear.addActionListener(this); 
 eraser.addActionListener(this); 
 pen.addActionListener(this); 
 drLine.addActionListener(this); 
 drCircle.addActionListener(this); 
 drRect.addActionListener(this); 
 openPic.addActionListener(this); 
 savePic.addActionListener(this); 
 colchooser.addActionListener(this); 

 颜色 = new Label("画笔颜色",Label.CENTER); 
 大小B = new Label("画笔大小",Label.CENTER); 
 大小E = new Label("橡皮大小",Label.CENTER); 
 //面板添加组件 
 toolPanel.add(openPic); 
 toolPanel.add(savePic); 

 toolPanel.add(pen); 
 toolPanel.add(drLine); 
 toolPanel.add(drCircle); 
 toolPanel.add(drRect); 

 toolPanel.add(颜色); toolPanel.add(ColChoice); 
 toolPanel.add(大小B); toolPanel.add(SizeChoice); 
 toolPanel.add(colchooser); 

 toolPanel.add(eraser); 
 toolPanel.add(大小E); toolPanel.add(EraserChoice); 

 toolPanel.add(clear); 
 //工具面板到APPLET面板 
 add(toolPanel,BorderLayout.NORTH); 

 setBounds(60,60,900,600); setVisible(true); 
 validate(); 
 //dialog for save and load 

 openPicture = new FileDialog(this,"打开图画",FileDialog.LOAD); 
 openPicture.setVisible(false); 
 savePicture = new FileDialog(this,"保存图画",FileDialog.SAVE); 
 savePicture.setVisible(false); 

 openPicture.addWindowListener(new WindowAdapter() 
 { 
 public void windowClosing(WindowEvent e) 
 { openPicture.setVisible(false); } 
 }); 

 savePicture.addWindowListener(new WindowAdapter() 
 { 
 public void windowClosing(WindowEvent e) 
 { savePicture.setVisible(false); } 
 }); 

 addWindowListener(new WindowAdapter() 
 { 
 public void windowClosing(WindowEvent e) 
 { System.exit(0);} 
 }); 

 } 

 public void paint(Graphics g) 
 { 
 Graphics2D g2d = (Graphics2D)g; 

 Point p1,p2; 

 n = paintInfo.size(); 

 if(toolFlag==2) 
 g.clearRect(0,0,getSize().width,getSize().height);//清除 

 for(int i=0; i<n ;i++){ 
 p1 = (Point)paintInfo.elementAt(i); 
 p2 = (Point)paintInfo.elementAt(i+1); 
 size = new BasicStroke(p1.boarder,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL); 

 g2d.setColor(p1.col); 
 g2d.setStroke(size); 

 if(p1.tool==p2.tool) 
 { 
 switch(p1.tool) 
 { 
 case 0://画笔 

 Line2D line1 = new Line2D.Double(p1.x, p1.y, p2.x, p2.y); 
 g2d.draw(line1); 
 break; 

 case 1://橡皮 
 g.clearRect(p1.x, p1.y, p1.boarder, p1.boarder); 
 break; 

 case 3://画直线 
 Line2D line2 = new Line2D.Double(p1.x, p1.y, p2.x, p2.y); 
 g2d.draw(line2); 
 break; 

 case 4://画圆 
 Ellipse2D ellipse = new Ellipse2D.Double(p1.x, p1.y, Math.abs(p2.x-p1.x) , Math.abs(p2.y-p1.y)); 
 g2d.draw(ellipse); 
 break; 

 case 5://画矩形 
 Rectangle2D rect = new Rectangle2D.Double(p1.x, p1.y, Math.abs(p2.x-p1.x) , Math.abs(p2.y-p1.y)); 
 g2d.draw(rect); 
 break; 

 case 6://截断，跳过 
 i=i+1; 
 break; 

 default : 
 }//end switch 
 }//end if 
 }//end for 
 } 

 public void itemStateChanged(ItemEvent e) 
 { 
 if(e.getSource()==ColChoice)//预选颜色 
 { 
 String name = ColChoice.getSelectedItem(); 

 if(name=="black") 
 {c = new Color(0,0,0); } 
 else if(name=="red") 
 {c = new Color(255,0,0);} 
 else if(name=="green") 
 {c = new Color(0,255,0);} 
 else if(name=="blue") 
 {c = new Color(0,0,255);} 
 } 
 else if(e.getSource()==SizeChoice)//画笔大小 
 { 
 String selected = SizeChoice.getSelectedItem(); 

 if(selected=="1") 
 { 
 con = 1; 
 size = new BasicStroke(con,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL); 

 } 
 else if(selected=="3") 
 { 
 con = 3; 
 size = new BasicStroke(con,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL); 

 } 
 else if(selected=="5") 
 {con = 5; 
 size = new BasicStroke(con,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL); 

 } 
 else if(selected=="7") 
 {con = 7; 
 size = new BasicStroke(con,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL); 

 } 
 else if(selected=="9") 
 {con = 9; 
 size = new BasicStroke(con,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL); 

 } 
 } 
 else if(e.getSource()==EraserChoice)//橡皮大小 
 { 
 String Esize = EraserChoice.getSelectedItem(); 

 if(Esize=="5") 
 { Econ = 5*2; } 
 else if(Esize=="9") 
 { Econ = 9*2; } 
 else if(Esize=="13") 
 { Econ = 13*2; } 
 else if(Esize=="17") 
 { Econ = 17*3; } 

 } 

 } 

 public void mouseDragged(MouseEvent e) 
 { 
 Point p1 ; 
 switch(toolFlag){ 
 case 0://画笔 
 x = (int)e.getX(); 
 y = (int)e.getY(); 
 p1 = new Point(x, y, c, toolFlag, con); 
 paintInfo.addElement(p1); 
 repaint(); 
 break; 

 case 1://橡皮 
 x = (int)e.getX(); 
 y = (int)e.getY(); 
 p1 = new Point(x, y, null, toolFlag, Econ); 
 paintInfo.addElement(p1); 
 repaint(); 
 break; 

 default : 
 } 
 } 

 public void mouseMoved(MouseEvent e) {} 

 public void update(Graphics g) 
 { 
 paint(g); 
 } 

 public void mousePressed(MouseEvent e) 
 { 
 Point p2; 
 switch(toolFlag){ 
 case 3://直线 
 x = (int)e.getX(); 
 y = (int)e.getY(); 
 p2 = new Point(x, y, c, toolFlag, con); 
 paintInfo.addElement(p2); 
 break; 

 case 4: //圆 
 x = (int)e.getX(); 
 y = (int)e.getY(); 
 p2 = new Point(x, y, c, toolFlag, con); 
 paintInfo.addElement(p2); 
 break; 

 case 5: //矩形 
 x = (int)e.getX(); 
 y = (int)e.getY(); 
 p2 = new Point(x, y, c, toolFlag, con); 
 paintInfo.addElement(p2); 
 break; 

 default : 
 } 
 } 

 public void mouseReleased(MouseEvent e) 
 { 
 Point p3; 
 switch(toolFlag){ 
 case 0://画笔 
 paintInfo.addElement(cutflag); 
 break; 

 case 1: //eraser 
 paintInfo.addElement(cutflag); 
 break; 

 case 3://直线 
 x = (int)e.getX(); 
 y = (int)e.getY(); 
 p3 = new Point(x, y, c, toolFlag, con); 
 paintInfo.addElement(p3); 
 paintInfo.addElement(cutflag); 
 repaint(); 
 break; 

 case 4: //圆 
 x = (int)e.getX(); 
 y = (int)e.getY(); 
 p3 = new Point(x, y, c, toolFlag, con); 
 paintInfo.addElement(p3); 
 paintInfo.addElement(cutflag); 
 repaint(); 
 break; 

 case 5: //矩形 
 x = (int)e.getX(); 
 y = (int)e.getY(); 
 p3 = new Point(x, y, c, toolFlag, con); 
 paintInfo.addElement(p3); 
 paintInfo.addElement(cutflag); 
 repaint(); 
 break; 

 default: 
 } 
 } 

 public void mouseEntered(MouseEvent e){} 

 public void mouseExited(MouseEvent e){} 

 public void mouseClicked(MouseEvent e){} 

 public void actionPerformed(ActionEvent e) 
 { 

 if(e.getSource()==pen)//画笔 
 {toolFlag = 0;} 

 if(e.getSource()==eraser)//橡皮 
 {toolFlag = 1;} 

 if(e.getSource()==clear)//清除 
 { 
 toolFlag = 2; 
 paintInfo.removeAllElements(); 
 repaint(); 
 } 

 if(e.getSource()==drLine)//画线 
 {toolFlag = 3;} 

 if(e.getSource()==drCircle)//画圆 
 {toolFlag = 4;} 

 if(e.getSource()==drRect)//画矩形 
 {toolFlag = 5;} 

 if(e.getSource()==colchooser)//调色板 
 { 
 Color newColor = JColorChooser.showDialog(this,"调色板",c); 
 c = newColor; 
 } 

 if(e.getSource()==openPic)//打开图画 
 { 

 openPicture.setVisible(true); 

 if(openPicture.getFile()!=null) 
 { 
 int tempflag; 
 tempflag = toolFlag; 
 toolFlag = 2 ; 
 repaint(); 

 try{ 
 paintInfo.removeAllElements(); 
 File filein = new File(openPicture.getDirectory(),openPicture.getFile()); 
 picIn = new FileInputStream(filein); 
 VIn = new ObjectInputStream(picIn); 
 paintInfo = (Vector)VIn.readObject(); 
 VIn.close(); 
 repaint(); 
 toolFlag = tempflag; 

 } 

 catch(ClassNotFoundException IOe2) 
 { 
 repaint(); 
 toolFlag = tempflag; 
 System.out.println("can not read object"); 
 } 
 catch(IOException IOe) 
 { 
 repaint(); 
 toolFlag = tempflag; 
 System.out.println("can not read file"); 
 } 
 } 

 } 

 if(e.getSource()==savePic)//保存图画 
 { 
 savePicture.setVisible(true); 
 try{ 
 File fileout = new File(savePicture.getDirectory(),savePicture.getFile()); 
 picOut = new FileOutputStream(fileout); 
 VOut = new ObjectOutputStream(picOut); 
 VOut.writeObject(paintInfo); 
 VOut.close(); 
 } 
 catch(IOException IOe) 
 { 
 System.out.println("can not write object"); 
 } 

 } 
 } 
 }//end paintboard 
 
 public class MyPaint {

		public static void main(String[] args) {
			 new paintboard("画图程序");

		}

	}

 
 
 
 
 
 
 
 
 
 