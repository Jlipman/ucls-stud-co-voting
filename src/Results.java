
import com.google.gdata.data.spreadsheet.*;
import java.util.*;

import java.io.*;

public class Results {

    //TODO make this at least somewhat efficient 
    private Drive results;
    static final String [] positions = {"President", "Vice President", "Cultural Union"};

    public Results(String username, String password) {
        results = new Drive(username, password, "Results");
    }

    public static void main(String link, String password) {
        Setup setup = new Setup();
        
        setup.getDriveVals(link,password);
        Drive results = new Drive(link, password, "Election");
        ArrayList[][] candidates = new ArrayList[4][2];
        

        String[] current = new String[4];
        
        for(int x=0; x<4;x++){
            for(int y=0; y<2; y++){
                if(y==0){
                    candidates[x][y]= new ArrayList<String>();
                }else{
                    candidates[x][y]= new ArrayList<Integer>();
                }
                
            }
        }

        ArrayList<CellEntry> drive = results.getList();

        for (int i = 1; true; i++) {
            System.out.println(i);
            for (int u = 0; u < 4; u++) {
                current[u] = get(u + 2, i, drive);
            }
            if (current[1].equals("stop")) {
                break;
            } else {
                for(int j=0; j<4; j++){
                    if (!current[j].equals("0")) {
                        boolean voteAdded = false;
                        for (int u = 0; u < candidates[j][0].size(); u++) {
                            if (candidates[j][0].get(u).equals(current[j])) {
                                candidates[j][1].set(u, (Integer)candidates[j][1].get(u) + 1);
                                voteAdded = true;
                            }
                        }
                        if (!voteAdded) {
                            candidates[j][0].add((String)(current[j]));
                            candidates[j][1].add(1);
                        }
                    }
                }
            }
        }

        
        //agregate the two cu columns
        
        for(int x=0; x<candidates[3][0].size(); x++){
            String cand=(String)candidates[3][0].get(x);
            for(int y=0; y<candidates[2][0].size(); y++){
               if(cand.equals(candidates[2][0].get(y))){
                   candidates[2][1].set(y,(Integer)candidates[3][1].get(x)+(Integer)(candidates[2][1].get(y)));
                   candidates[3][0].remove(x);
                   candidates[3][1].remove(x);
                   x--;
                   break;
               }
            }
        }   
        for(int f=0; f<candidates[3][0].size(); f++){
            candidates[2][0].add(candidates[3][0].get(f));
            candidates[2][1].add(candidates[3][1].get(f));
        }
        
        
        System.out.println("finished fethching data");
        System.out.println("calculating winners");
        FileWriter writer = null;
        try {
            File file = new File("Winners.txt");
            file.createNewFile();
            writer = new FileWriter(file);
           writer.write("Election Results\n");
        } catch (Exception e) {
            System.out.println(e);
        }
        int biggest=0;
        int indexofbiggest = 0;
        for(int j=0; j<4;j++){
            if(j==3){
                candidates[2][0].remove(indexofbiggest);
                candidates[2][1].remove(indexofbiggest);

                biggest = 0;
                indexofbiggest = 0;
                for (int i = 0; i < candidates[2][1].size(); i++) {
                    if ((Integer)candidates[2][1].get(i) > biggest) {
                        biggest = (Integer)candidates[2][1].get(i);
                        indexofbiggest = i;
                    }
                }
                try {
                    writer.write(positions[2] + " " + candidates[2][0].get(indexofbiggest) + "\n");
                } catch (Exception e) {
                    System.out.println(e);
                }
                continue;
            }
            biggest = 0;
            indexofbiggest = 0;
            for (int i = 0; i < candidates[j][1].size(); i++) {
                if ((Integer)candidates[j][1].get(i) > biggest) {
                    biggest = (Integer)candidates[j][1].get(i);
                    indexofbiggest = i;
                }
            }
            try {
                writer.write(positions[j] + " " + candidates[j][0].get(indexofbiggest) + "\n");
            } catch (Exception e) {
                System.out.println(e);
            }
            System.out.println(candidates[j][0].get(indexofbiggest));
            for (int y = 0; y < candidates[j][0].size(); y++) {
                results.set((j*2)+6, y + 1, candidates[j][0].get(y).toString());
            }
            for (int y = 0; y < candidates[j][1].size(); y++) {
                results.set((j*2)+7, y + 1, "" + candidates[j][1].get(y).toString());
            }
            
        }
        
        
        try {
            writer.flush();
            writer.close();
        } catch (Exception e) {
            System.out.println(e);
        }

        System.out.println("go online to check winners");
    }

    private int searchForEmpty(int col) {
        int y = 0;
        while (!(results.get(col, y).equals("0"))) {
            y++;
        }
        return y;
    }

    private static String get(int x, int y, ArrayList<CellEntry> list) {
        String values = "";        
        for (CellEntry cell : list) {
            if (cell.getId().substring(cell.getId().lastIndexOf('/') + 1).equals("R" + y + "C" + x)) {
                values = cell.getCell().getInputValue();
                break;
            }
        }
        return values;
    }
    
}

