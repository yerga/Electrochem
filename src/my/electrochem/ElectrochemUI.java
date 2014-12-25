/*
 * Copyright (C) 2014 Daniel Martín-Yerga <dyerga@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package my.electrochem;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import mr.go.sgfilter.ContinuousPadder;
import mr.go.sgfilter.SGFilter;
import static my.electrochem.FindPeaks.peakdet;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


public class ElectrochemUI extends javax.swing.JFrame {

    /**
     * Creates new form ElectrochemUI
     */
    
    double x1, x2, y1, y2;
    boolean click = true;
    private final ChartPanel chartPanel;
    private XYSeriesCollection dataset1;
    
    public ElectrochemUI() {
        super("Electrochem App");
        initComponents();
        
        chartPanel = createChartPanel();
                 
        chartPanel.addChartMouseListener(new ChartMouseListener() {

            @Override
            public void chartMouseClicked(ChartMouseEvent e) {
                //System.out.println(e.getEntity() + " " + e.getEntity().getArea());           
                //System.out.println("X:"+e.getTrigger().getX()+"Y:"+e.getTrigger().getY());
                //XYPlot xyPlot2 = e.getChart().getXYPlot();
                  // Problem: the coordinates displayed are the one of the previously selected point !
                //System.out.println(xyPlot2.getDomainCrosshairValue() + " "
                //        + xyPlot2.getRangeCrosshairValue());
               
                
                //createLineAnn(e.getChart().getXYPlot(), x1, y1, x2, y2);
                //createLineAnn(e.getChart().getXYPlot(), x1, y1, x2, y2);
                //if (!(click)) {
                //    e.getChart().getXYPlot().clearAnnotations();
                //}
                
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {
               
            }
        });

        jPanel1.add(chartPanel, BorderLayout.CENTER);
        jPanel1.validate();    
        
    }
    
    
    private void readGPESfile(File fileOpen, ChartPanel chartPanel, 
            boolean overlay) throws FileNotFoundException {
        
        ArrayList<Float> potentials = new ArrayList<>();
        ArrayList<Float> currents = new ArrayList<>();
        BufferedReader br = null;
        
        try {
            String sCurrentLine;
            //Path path = FileSystems.getDefault().getPath("libs", "filename.txt");
            //br = new BufferedReader(new FileReader(path.toString()));
            br = new BufferedReader(new FileReader(fileOpen.getAbsolutePath()));
            String ext = getExtension(fileOpen.getAbsolutePath());            
            
            //TODO: 3-column data for DPV/SWV etc.
            int i=0;
            while ((sCurrentLine = br.readLine()) != null) {
                if ((i != 0)&&(i != 1)) {
                    String[] arr = sCurrentLine.split(" ");
                    //System.out.println("Array:"+Arrays.toString(arr));
                    if (arr.length == 3) {
                        //System.out.println("Potential:"+arr[1]);
                        //System.out.println("Current:"+arr[2]);                         
                        potentials.add(Float.parseFloat(arr[1]));
                        currents.add(Float.parseFloat(arr[2]));
                    } else if (arr.length == 5) {
                        potentials.add(Float.parseFloat(arr[2]));
                        currents.add(Float.parseFloat(arr[4]));
                    } else if ((arr.length == 4)&&(arr[2].equals(arr[0]))) {
                        potentials.add(Float.parseFloat(arr[1]));
                        currents.add(Float.parseFloat(arr[3]));
                    } else  {
                        potentials.add(Float.parseFloat(arr[2]));                          
                        currents.add(Float.parseFloat(arr[3]));
                    }                        
                }

                i++;
            } 
            System.out.println("potentials:"+potentials);
            System.out.println("currents:"+currents);
            
            //TODO: show technique at the UI
            //TODO: overlay not possible for different techniques         
            ArrayList<String> labels = getGPESXYlabels(fileOpen, ext);
        
            ValueAxis xaxis = chartPanel.getChart().getXYPlot().getDomainAxis();
            ValueAxis yaxis = chartPanel.getChart().getXYPlot().getRangeAxis();
            xaxis.setLabel(labels.get(0));
            yaxis.setLabel(labels.get(1));
            
            
            XYSeries series = getXYSeriesfromiE(potentials, currents, 
                    fileOpen.getName());
            if (!overlay) {
                //System.out.println("no overlay gpes open");
                dataset1.removeAllSeries();
            }
            
            dataset1.addSeries(series);
            jPanel1.validate();
              
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }

    }

    private void createLineAnn(XYPlot plot, double a1, double b1, double a2, double b2) {
        if (!(a1 == 0.0)) {
            XYLineAnnotation ann1 = new XYLineAnnotation(a1, b1, a2, b2);
            plot.addAnnotation(ann1); 
            x1 = -423.0;
        } 
    }
    private ChartPanel createChartPanel() {
        //creates a line chart object
        //returns the chart panel
        String chartTitle = "i-E curve";
        String xAxisLabel = "E (V)";
        String yAxisLabel = "i (A)";
 
        dataset1 = createEmptyDataset();
 
        JFreeChart chart = ChartFactory.createScatterPlot(chartTitle,
            xAxisLabel, yAxisLabel, dataset1);        
        
        XYPlot plot = chart.getXYPlot();
        
        plot.setDomainCrosshairVisible(false);
        plot.setRangeCrosshairVisible(false);
        plot.setDomainCrosshairLockedOnData(false);
        plot.setRangeCrosshairLockedOnData(false);
        
        /*chart.addProgressListener(new ChartProgressListener() {
            @Override
            public void chartProgress(ChartProgressEvent cpe) {
                if (cpe.getType() == ChartProgressEvent.DRAWING_FINISHED) {
                    //System.out.println("Click event!!");
                    XYPlot xyPlot2 = cpe.getChart().getXYPlot();
                    System.out.println("drawing finished");
                    System.out.println("Xreal:"+xyPlot2.getDomainCrosshairValue()
                           +"Yreal:"+xyPlot2.getRangeCrosshairValue());
                    if (click) {
                        System.out.println("click true");
                        if (x1 == -423.0) {
                            x1 = 0.0;
                            y1 = 0.0;
                            
                        }
 
                        if (x1 == 0.0 && y1 == 0.0) {
                            System.out.println("print 0,0");
                            click = true;
                            x1 = xyPlot2.getDomainCrosshairValue();
                            y1 = xyPlot2.getRangeCrosshairValue();
                            //xyPlot2.clearAnnotations();
                        } else {
                            xyPlot2.clearAnnotations();
                            System.out.println("true-false");
                            click = false;
                            x1 = xyPlot2.getDomainCrosshairValue();
                            y1 = xyPlot2.getRangeCrosshairValue();
                        }
                      
                             
                    } else {
                        System.out.println("click false");
                       x2 = xyPlot2.getDomainCrosshairValue();
                       y2 = xyPlot2.getRangeCrosshairValue();
                       createLineAnn(xyPlot2, x1, y1, x2, y2);
                       click = true;
                   }
               }
           }
        });*/
        
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
 
        // sets paint color for each series
        //renderer.setSeriesPaint(0, Color.RED);

        // sets thickness for series (using strokes)
        //renderer.setSeriesStroke(0, new BasicStroke(5.0f));
        renderer.setBaseLinesVisible(true);
        //renderer.setSeriesLinesVisible(0, true);
 
        //renderer.setBaseShapesFilled(true);
        renderer.setBaseShapesVisible(false);
        //srenderer.setDrawSeriesLineAsPath(false);
        
        plot.setOutlinePaint(Color.BLUE);
        plot.setOutlineStroke(new BasicStroke(2.0f));
        plot.setBackgroundPaint(Color.DARK_GRAY);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        plot.setRenderer(renderer);
        
        return new ChartPanel(chart);
    }
    
    private XYSeriesCollection createEmptyDataset() {        
        XYSeriesCollection dataset = new XYSeriesCollection();
        return dataset;
    }
    
    private XYSeries getXYSeriesfromiE(ArrayList<Float> potentials, 
            ArrayList<Float> currents, String xytitle) {
        
        XYSeries series1 = new XYSeries(xytitle, false);
        for (int i = 0; i < potentials.size(); i++) {
            series1.add(potentials.get(i),currents.get(i));
        }
        
        return series1;
        
    }
    public static boolean isOSX() {
        String osName = System.getProperty("os.name");
        return osName.contains("OS X");
    }
    
    public static String getExtension(String fileName) {
        String extension = "";

        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            extension = fileName.substring(i+1);
        }
        return extension;
    }
    
    public static ArrayList<String> getGPESXYlabels(File fileOpen, String extension) {
        String newfile = null;
        String sCurrentLine;
        BufferedReader br = null;
        ArrayList<String> labels = new ArrayList<>();;
        
        switch (extension) {
            case "ocw":
                newfile = fileOpen.getAbsolutePath().replaceAll("ocw", "ici");
                break;
            case "oxw":
                newfile = fileOpen.getAbsolutePath().replaceAll("oxw", "ixi");
                break;
            case "oew":
                newfile = fileOpen.getAbsolutePath().replaceAll("oew", "iei");
                break;
        }
        
        //System.out.println("newfile:"+newfile);
        
        try {
            br = new BufferedReader(new FileReader(newfile));
            System.out.println("opened file");
            int i=0;
            while ((sCurrentLine = br.readLine()) != null) {
                if (i == 3) {
                    String[] xl = sCurrentLine.split("=");
                    labels.add(xl[1]);                    
                    System.out.println("xlabel:"+xl[1]);
                } else if (i == 18) {
                    String[] yl = sCurrentLine.split("=");
                    labels.add(yl[1]);
                    System.out.println("ylabel:"+yl[1]);
                    break;
                }
                i++;
            }
        
        } catch (IOException e) {
            //e.printStackTrace();
            labels.add("Xaxis");
            labels.add("Yaxis");
            //TODO: add axis by file format, for CM: standards
            //TODO:log stacktrace+useful data
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex){
                ex.printStackTrace();
            }
        }
        
        return labels;
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSeparator1 = new javax.swing.JSeparator();
        fileChooser = new javax.swing.JFileChooser();
        jPanel1 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jToggleButton1 = new javax.swing.JToggleButton();
        OverlayCurvesBtn = new javax.swing.JButton();
        autoMeasureBtn = new javax.swing.JButton();
        SmoothBtn = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        OpenGPES = new javax.swing.JMenuItem();
        Exit = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        fileChooser.setCurrentDirectory(new java.io.File("/Users/yerga/Development/GPESfiles"));
        fileChooser.setDialogTitle("Open file...");
        fileChooser.setFileFilter(null);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(800, 600));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new java.awt.BorderLayout());
        getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        jToolBar1.setRollover(true);

        jToggleButton1.setText("Measure");
        jToggleButton1.setFocusable(false);
        jToggleButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });
        jToolBar1.add(jToggleButton1);

        OverlayCurvesBtn.setText("Overlay");
        OverlayCurvesBtn.setFocusable(false);
        OverlayCurvesBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        OverlayCurvesBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        OverlayCurvesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OverlayCurvesBtnActionPerformed(evt);
            }
        });
        jToolBar1.add(OverlayCurvesBtn);

        autoMeasureBtn.setText("Auto");
        autoMeasureBtn.setFocusable(false);
        autoMeasureBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        autoMeasureBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        autoMeasureBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoMeasureBtnActionPerformed(evt);
            }
        });
        jToolBar1.add(autoMeasureBtn);

        SmoothBtn.setText("Smooth");
        SmoothBtn.setFocusable(false);
        SmoothBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        SmoothBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        SmoothBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SmoothBtnActionPerformed(evt);
            }
        });
        jToolBar1.add(SmoothBtn);

        getContentPane().add(jToolBar1, java.awt.BorderLayout.PAGE_START);

        jMenu1.setText("File");

        OpenGPES.setText("Open GPES file...");
        OpenGPES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OpenGPESActionPerformed(evt);
            }
        });
        jMenu1.add(OpenGPES);

        Exit.setText("Exit");
        Exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ExitActionPerformed(evt);
            }
        });
        jMenu1.add(Exit);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton1ActionPerformed


    static class ExtensionFileFilter 
        extends javax.swing.filechooser.FileFilter {

        private List<String> extensions;
        private String description;

        public ExtensionFileFilter(String[] exts, String desc) {
            if (exts != null) {
                extensions = new ArrayList<>();

                for (String ext : exts) {
                    extensions.add(
                        ext.replace(".", "").trim().toLowerCase()
                    );
                }
            } 
            description = (desc != null) ? desc.trim() : "Custom File List";
        }

        @Override
        public boolean accept(File f) {
            if (f.isDirectory()) return true;

            if (extensions == null) return false;

            for (String ext : extensions) {
                if (f.getName().toLowerCase().endsWith("." + ext))
                    return true;
            }

            return false;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
    
    
    private void OpenGPESAction(java.awt.event.ActionEvent evt, boolean overlay) {
        for (FileFilter item: fileChooser.getChoosableFileFilters()) {
            fileChooser.removeChoosableFileFilter(item);
        }
        fileChooser.addChoosableFileFilter(new ExtensionFileFilter(new String[] {".ocw"}, "CV-LSV GPES (.ocw)"));
        fileChooser.addChoosableFileFilter(new ExtensionFileFilter(new String[] {".oxw"}, "CM GPES (.oxw)"));
        fileChooser.addChoosableFileFilter(new ExtensionFileFilter(new String[] {".oew"}, "VA GPES (.oew)"));
        fileChooser.setAcceptAllFileFilterUsed(true);
        
        int returnVal = fileChooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                readGPESfile(file, chartPanel, overlay);

            } catch (IOException ex) {
                System.out.println("problem accessing file"+file.getAbsolutePath());
            }
        } else {
            System.out.println("File access cancelled by user.");
        }
         
    }
    
    private void OpenGPESActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OpenGPESActionPerformed
        OpenGPESAction(evt, false);        
    }//GEN-LAST:event_OpenGPESActionPerformed

    private void ExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ExitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_ExitActionPerformed

    private void OverlayCurvesBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OverlayCurvesBtnActionPerformed
        OpenGPESAction(evt, true);
        
    }//GEN-LAST:event_OverlayCurvesBtnActionPerformed

    private void autoMeasureBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoMeasureBtnActionPerformed
        double xydata[][] = dataset1.getSeries(0).toArray();
        ArrayList<Float> v = new ArrayList<>();
        ArrayList<Float> x = new ArrayList<>();
        
        //System.out.println("xydata0: "+xydata[0].length);
        
        //System.out.println("lenght: "+xydata.length);
        
        for (int i = 0; i < xydata[0].length; i++) {
            //System.out.println("x values: "+xydata[0][i]);
            //System.out.println("y values: "+xydata[1][i]);
            //System.out.println("xydata:"+xydata[0][1]);
            //System.out.println("x point:"+xydata.toArray()[0].);
            //System.out.println("y point:"+xydata.get);
            v.add((float)xydata[1][i]);
            x.add((float)xydata[0][i]);
        }
        //System.out.println("data"+xydata);
        //TODO optimize delta value
        ArrayList<ArrayList<ArrayList<Float>>> coll = peakdet(v, (float)1e-6, x);
        System.out.println("coll data: "+coll);
        
    }//GEN-LAST:event_autoMeasureBtnActionPerformed

    private void SmoothBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SmoothBtnActionPerformed
        
        double xydata[][] = dataset1.getSeries(0).toArray();
        
        double[] coeffs = SGFilter.computeSGCoefficients(5, 5, 4);
        ContinuousPadder padder1 = new ContinuousPadder();
        SGFilter sgFilter = new SGFilter(5, 5);
        sgFilter.appendPreprocessor(padder1);
        

        float[] floatArray = new float[xydata[1].length];
        
        for (int i = 0 ; i < xydata[1].length; i++)
        {
            floatArray[i] = (float) xydata[1][i];
        }
        
        float[] smooth = sgFilter.smooth(floatArray, coeffs);
        
        
        ArrayList<Float> currents = new ArrayList<>();
        for (int j = 0; j<smooth.length; j++) {
            currents.add(smooth[j]);
        }
        
        
        ArrayList<Float> potentials = new ArrayList<>();
        for (int j = 0; j<xydata[0].length; j++) {
            potentials.add((float)xydata[0][j]);
        }
        
        XYSeries series = getXYSeriesfromiE(potentials, currents, "smooth");

        //dataset1.removeAllSeries();
        dataset1.addSeries(series);
        jPanel1.validate();
        
    }//GEN-LAST:event_SmoothBtnActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if (!isOSX()) {
                    if ("Nimbus".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                } else {
                    System.setProperty("apple.laf.useScreenMenuBar", "true");
                    if ("Aqua".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ElectrochemUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ElectrochemUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ElectrochemUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ElectrochemUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ElectrochemUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem Exit;
    private javax.swing.JMenuItem OpenGPES;
    private javax.swing.JButton OverlayCurvesBtn;
    private javax.swing.JButton SmoothBtn;
    private javax.swing.JButton autoMeasureBtn;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
