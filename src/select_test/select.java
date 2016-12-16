package select_test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import java.io.FileReader;
import java.util.Iterator;
import com.google.*;
import com.google.gson.Gson;


public class select implements Runnable{

	private URL url;
	private URLConnection con;
	private HttpURLConnection http;
	private String url_request,db_type,query,request;
	private OutputStreamWriter os;
	private Thread t;
	private long start_time;
	private long end_time;
	private int debug=0;
	private int left_edge;
	private File file;
	private int right_edge;
	private int thd_id;
	private int start_line;
	private database mysql;
	
	public void startTimer(String s){
		start_time=System.currentTimeMillis();
    	System.out.println(s);
	}
	
	public void timetoExecute(String s){
		end_time=System.currentTimeMillis();
        System.out.println(s+(end_time-start_time));
	}
	
	
	private void edge_calc(){
		left_edge=thd_id*start_line;
		right_edge=start_line+left_edge;
		System.out.println("left_edge:"+left_edge+"thd_id:"+thd_id);
		System.out.println("rigth_edge:"+right_edge+"thd_id"+thd_id);
	}
	
	public select(String r,int d, File f,int id,int start_line) throws IOException{
		mysql=new database();
		request=r;
		debug=d;
		file=f;
		thd_id=id;
		this.start_line=start_line;
		edge_calc();
		/*
		this.query=query;
		url = new URL(request);	
		con = url.openConnection();
		http = (HttpURLConnection)con;
		http.setRequestMethod("POST"); 
		http.setDoOutput(true);
		http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		os= new OutputStreamWriter(http.getOutputStream());
	    */
	}

	public void run(){
		
	    startTimer("Start thread: "+Integer.toString(thd_id));
	    
	    int row_index=0;
	    @SuppressWarnings("resource")
		Scanner scanner=null;
		try {
			scanner = new Scanner(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
	    while (scanner.hasNextLine() && row_index<=left_edge){
        	scanner.nextLine();
        	row_index++;
        }
	    String sqlstart="INSERT INTO keywords";
	    sqlstart+="(keyword, competetion, ams, lms, impressions, clicks, cost, ctr, country, average_cpc, average_position, cpc, m1, m2, m3, m4, m5, m6, m7, m8, m9, m10, m11, m12)";
	    sqlstart+="VALUES ";
	    String query="";
	    String api_output="";
		Gson gson = new Gson();
        while (scanner.hasNextLine() && row_index<=right_edge ) {
            String line = scanner.nextLine();
            query="";
		 try (java.util.Scanner s = new java.util.Scanner(new java.net.URL(request+line+"&loc=italy").openStream())) {

			  
			 if(debug>0){
				 api_output=s.useDelimiter("\\A").next();
				 KeywordStat[] response= gson.fromJson(api_output, KeywordStat[].class);
				 System.out.println(response[0].m1);
				 
		    }
		 } catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
		//timetoExecute("Finisced thread id: ");
		/*
		try {
			System.out.println(query);
			os.write(query);
			os.flush();
			System.out.println(http.getResponseMessage());
			os.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	
	public void start (){
		t=new Thread(this);
		t.start();
	}

	public void wait_done() {
		// TODO Auto-generated method stub
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
