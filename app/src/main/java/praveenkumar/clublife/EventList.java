package praveenkumar.clublife;

import java.util.ArrayList;

/**
 * Created by Praveen kumar on 05/08/2015.
 */
public class EventList {
    ArrayList<String> listString;
    ArrayList<String> idReference;
    public EventList(){
        listString=new ArrayList<>();
        idReference=new ArrayList<>();
    }

    public void add(String id,String lable){
        listString.add(lable);
        idReference.add(id);
    }

    public String getLable(int index){
        return listString.get(index);
    }
    public String getId(int index){
        return idReference.get(index);
    }

    public ArrayList<String> getListString(){
        return listString;
    }

    public EventList filterList(String str){
        if(str.equals(""))return this;
        EventList tEventList=new EventList();
        for(int i=0;i<listString.size();i++){
            if(listString.get(i).contains(str)){
                tEventList.add(idReference.get(i),listString.get(i));
            }
        }
        return tEventList;
    }
}
