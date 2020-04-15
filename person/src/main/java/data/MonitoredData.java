package data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MonitoredData {
	private String startTime;
	private String endTime;
	private String activity;
	
	public MonitoredData(String startTime, String endTime, String activity){
		this.startTime=startTime;
		this.endTime=endTime;
		this.activity=activity;
	}
	
	/**
	 * Method that splits the input data from txt and create a list of strings with each line. Then the list is traced and the list of monitoredData is formed
	 * @return monitoredData the list of monitoredData objects
	 */
	public static List<MonitoredData> readData()  
	{
		String fileName = "Activities.txt";
		List<String> list = new ArrayList<>();
		List<MonitoredData> monitoredData =new ArrayList<MonitoredData>();		
		try (Stream<String> stream = Files.lines(Paths.get(fileName))) {	
		list=stream.flatMap((line -> Stream.of(line.split("		")))).collect(Collectors.toList());	
		for(int i=0; i<list.size(); i+=3) {
			MonitoredData m=new MonitoredData(list.get(i), list.get(i+1), list.get(i+2));
			monitoredData.add(m);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return monitoredData;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public String getEndTime() {
		return endTime;
	}
	
	public String getActivity() {
		return activity;
	}
}
