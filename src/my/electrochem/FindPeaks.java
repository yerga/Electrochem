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


import java.util.ArrayList;

/**
 *
 * @author Daniel Martín-Yerga <dyerga@gmail.com>
 */
public class FindPeaks {
    public static ArrayList<ArrayList<ArrayList<Float>>> 
        peakdet(ArrayList<Float> v, Float delta, ArrayList<Float> x) {
        
        ArrayList<ArrayList<ArrayList<Float>>> collection = new ArrayList<ArrayList<ArrayList<Float>>>();
        ArrayList<ArrayList<Float>> maxlist = new ArrayList<ArrayList<Float>>();
        ArrayList<ArrayList<Float>> minlist = new ArrayList<ArrayList<Float>>();        
        ArrayList<Float> maxtab = new ArrayList<>();
        ArrayList<Float> mintab = new ArrayList<>();
        ArrayList<Float> mxtab = new ArrayList<>();
        ArrayList<Float> mntab = new ArrayList<>();        
        
        
        if (v.size() != x.size()) {
            System.out.println("v y x should be same length");
        }
        
        if (delta.isNaN()) {
            System.out.println("delta should be a number");
        }
        
        if (delta < 0) {
            System.out.println("delta should be > 0");
        }
        
        double mn = Double.POSITIVE_INFINITY;
        double mx = Double.NEGATIVE_INFINITY;
        
        //System.out.println("pos inf:"+mn);
        
        float mn1;
        float mx1;

        float mnpos = Float.NaN;
        float mxpos = Float.NaN;
    
        boolean lookformax = true;
                
        
        for (int i = 0; i < v.size(); i++) {
            double this1 = (double) v.get(i);
            //System.out.println("this:"+this1);
//            if (this1 < mx) {
//                System.out.println("this1 less than mx:" +mx);
//            }
            if (this1 > mx) {
                mx = this1;
                mxpos = x.get(i);
            }
            if (this1 < mn) {
                mn = this1;
                mnpos = x.get(i);
            }
            
            if (lookformax) {
                //System.out.println("lookformax");
                //System.out.println("this1: "+this1);
                //System.out.println("mx-delta: "+(mx-delta));
                //System.out.println("mn-delta: "+(mn-delta));
                if (this1 < mx-delta) {
                    //System.out.println("en mxdelta");
                    maxtab.add(mxpos);
                    mx1 = (float) mx;
                    mxtab.add(mx1);
                    mn = this1;
                    mnpos = x.get(i);
                    lookformax = false;
                }
            } else {
                if (this1 > mn-delta) {
                    mintab.add(mnpos);
                    mn1 = (float) mn;
                    mntab.add(mn1);
                    mx = this1;
                    mxpos = x.get(i);
                    lookformax = true;
                }
            }
        }
        
        maxlist.add(maxtab);
        maxlist.add(mxtab);
        minlist.add(mintab);
        minlist.add(mntab);
        collection.add(maxlist);
        collection.add(minlist);
        
        
        return collection;
       
              
    }
        
    public static void main(String args[]) {
        System.out.println("hello world");

        
        ArrayList <Float> v = new ArrayList<>();
        v.add((float)1);
        v.add((float)2);
        v.add((float)3);
        v.add((float)4);
        v.add((float)5);
        v.add((float)6);
        v.add((float)5);
        v.add((float)4);
        v.add((float)7);
        v.add((float)8);
        v.add((float)9);
        v.add((float)3);
        v.add((float)1);
        v.add((float)4);
        v.add((float)6);
        v.add((float)8);
        v.add((float)9);
        v.add((float)7);
        v.add((float)4);
        v.add((float)1);
        
        ArrayList <Float> x = new ArrayList<>();
        x.add((float)0);
        x.add((float)1);
        x.add((float)2);
        x.add((float)3);
        x.add((float)4);
        x.add((float)5);
        x.add((float)6);
        x.add((float)7);
        x.add((float)8);
        x.add((float)9);
        x.add((float)10);
        x.add((float)11);
        x.add((float)12);
        x.add((float)13);
        x.add((float)14);
        x.add((float)15);
        x.add((float)16);
        x.add((float)17);
        x.add((float)18);
        x.add((float)19);        

        System.out.println("vsize:"+v.size());
        System.out.println("xsize:"+x.size());
        
        ArrayList<ArrayList<ArrayList<Float>>> coll = peakdet(v, (float)0.5, x);
        System.out.println("coll:"+coll);
        
    }
     
}
