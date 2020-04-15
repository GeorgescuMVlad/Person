package data;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class Results {
	private static List<MonitoredData> monitoredData =new ArrayList<MonitoredData>();
		
	/**
	 * @return days the distinct days that have been monitorized 
	 */
	public static List<String> days() {
		List<String> days =new ArrayList<String>();
		String d;
		for(int i=0; i<monitoredData.size(); i++){
			d=Character.toString(monitoredData.get(i).getStartTime().charAt(8)) + Character.toString(monitoredData.get(i).getStartTime().charAt(9));
			days.add(d);
		}	
		days=days.stream().distinct().collect(Collectors.toList());
		return days;
	}
	
	/**
	 * Method to get all the distinct activities that have been monitorized
	 * @param monitoredData the list of monitored objects
	 * @return activities the activities that have been monitorized
	 */
	public static List<String> activities(List<MonitoredData> monitoredData){
		List<String> activities =new ArrayList<String>();
		for(int i=0; i<monitoredData.size(); i++) {
			activities.add(monitoredData.get(i).getActivity());
		}
		activities=activities.stream().distinct().collect(Collectors.toList());
		return activities;
	}
	
	/**
	 * Method to count the number of days that have been monitorized
	 * @param monitoredData the list of monitored objects
	 * @return countDays the nr of days that have been monitorized
	 */
	public static long countNrDays(List<MonitoredData> monitoredData) {
		long countDays=0;
		List<String> days =new ArrayList<String>();
		String d;
		for(int i=0; i<monitoredData.size(); i++){
			d=Character.toString(monitoredData.get(i).getStartTime().charAt(8)) + Character.toString(monitoredData.get(i).getStartTime().charAt(9));
			days.add(d);
		}	
		countDays=days.stream().distinct().count();
		return countDays;		
	}
	
	/**
	 * Method to count how many times appeared each activity during the monitored period
	 * @param monitoredData the list of monitored objects
	 * @return map a hash map that maps each activity with the number of apparitions
	 */
	public static HashMap<String, Integer> activitiesCount(List<MonitoredData> monitoredData)
	{
		List<String> activities =new ArrayList<String>();
		List<String> distinctActivities =new ArrayList<String>();
		HashMap<String, Integer> map = new HashMap<>();
		long countActivities=0;
		for(int i=0; i<monitoredData.size(); i++){
			activities.add(monitoredData.get(i).getActivity());
		}
		distinctActivities=activities(monitoredData);
		for(int j=0; j<distinctActivities.size(); j++){
			String s=distinctActivities.get(j);
			countActivities=activities.stream().filter(s::equals).count();
			map.put(s, (int)countActivities);
		}
		return map;
	}
	
	/**
	 * Method to count how many times appeared each activity for each day during the monitored period
	 * @param monitoredData the list of monitored objects
	 * @return list a list containing hash maps that map each activity with its number of apparitons
	 */
	public static ArrayList countActivitiesPerDay(List<MonitoredData> monitoredData)
	{
		List<String> days =new ArrayList<String>();
		List<MonitoredData> monitoredDataDay=new ArrayList<MonitoredData>();
		HashMap<String, Integer> map = new HashMap<>();
		ArrayList list = new ArrayList();
		String d;
		for(int i=0; i<monitoredData.size(); i++){
			d=Character.toString(monitoredData.get(i).getStartTime().charAt(8)) + Character.toString(monitoredData.get(i).getStartTime().charAt(9));
			days.add(d);			
		}	
		for(int i=0; i<days.size(); i++) {
			monitoredDataDay.add(monitoredData.get(i));
			if(i<days.size()-1){
				if(!days.get(i).equals(days.get(i+1))){
					map=activitiesCount(monitoredDataDay);
					list.add(map);
					monitoredDataDay=new ArrayList<MonitoredData>();
				}
			}else {map=activitiesCount(monitoredDataDay);
					list.add(map);
					}	
		}
		return list;
	}
	
	/**
	 * Method to get the time from the input data
	 * @param s a string containing activity start time or end time
	 * @return time a string containing only the time, not the date and time
	 */
	public static String getTimeFromList(String s) {
		String time="";
		for(int i=11; i<19; i++) {
			time+=Character.toString(s.charAt(i));
		}
		return time;
	}
	
	/**
	 * Method to get the duration of each line's activity
	 * @param monitoredData the list of monitored objects
	 */
	public static void durationActivityEachLine(List<MonitoredData> monitoredData){
		try {
			PrintWriter out = new PrintWriter("ActivityDurationEachLine.txt");
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				
				monitoredData.stream().forEach(item->{
					try {
						long difference=format.parse(item.getEndTime()).getTime() - format.parse(item.getStartTime()).getTime();
						long diffSeconds = difference / 1000 % 60;
						long diffMinutes = difference / (60 * 1000) % 60;
						long diffHours = difference / (60 * 60 * 1000) % 24;
						String res=Long.toString(diffHours)+":"+Long.toString(diffMinutes)+":"+Long.toString(diffSeconds);
						out.println("Activity: " + item.getActivity() + "  lasted:  " + res);
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
				});
				
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * Method to get the total duration of each activity
	 * @param monitoredData the list of monitored objects
	 */
	public static void durationEachActivityOnMonitoredPeriod(List<MonitoredData> monitoredData) {
		List<String> activities = activities(monitoredData);
		List<Long> diff= new ArrayList<Long>();
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {	PrintWriter out = new PrintWriter("ActivityDurationOverEntirePeriod.txt");
			activities.stream().forEach(it->{
				diff.clear();
				monitoredData.stream().forEach(item->{
					try {
						if(it.equals(item.getActivity())) {
							long difference=format.parse(item.getEndTime()).getTime() - format.parse(item.getStartTime()).getTime();
							diff.add(difference);		
						}	
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
				});
				long difference=diff.stream().collect(Collectors.summingLong(Long::longValue));
				long diffSeconds = difference / 1000 % 60;
				long diffMinutes = difference / (60 * 1000) % 60;
				long diffHours = difference / (60 * 60 * 1000) % 24;
				long diffDays = difference / (24 * 60 * 60 * 1000);
				String res=Long.toString(diffDays)+":"+Long.toString(diffHours)+":"+Long.toString(diffMinutes)+":"+Long.toString(diffSeconds);
				out.println("Activity: " + it.replaceAll("\\s","") + "  lasted:  " + res);
			});
			out.close();
			}catch (FileNotFoundException e) {
				e.printStackTrace();
			}		
		}

	public static void main(String[] args) {
		monitoredData=MonitoredData.readData();
		HashMap<String, Integer> map = new HashMap<>();
		ArrayList list = new ArrayList();
		List<String> days=days();
		long nrDays=countNrDays(monitoredData);
		map=activitiesCount(monitoredData);
		try {
			PrintWriter out = new PrintWriter("ResultsOfFirst3Tasks.txt");
			out.println("RESULTS Of First 3 Tasks: ");
			out.println("Number of days monitored: " + nrDays);
			for(String s: map.keySet()){
				out.println("Activity: " + s.replaceAll("\\s","") + " appeared: " + map.get(s) + " times");
			}			
			list=countActivitiesPerDay(monitoredData);
			for(int i=0; i<list.size(); i++) {
				HashMap<String, Integer> map2=(HashMap<String, Integer>) list.get(i);
				out.print(" On day " + days.get(i));
				for(String s: map2.keySet()) {
					out.println( " Activity: " + s.replaceAll("\\s","") + " appeared: " + map2.get(s) + " times");
				}
			}
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		durationActivityEachLine(monitoredData);
		durationEachActivityOnMonitoredPeriod( monitoredData);
	}
}
