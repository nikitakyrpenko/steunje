package com.negeso.module.statistics;

import java.util.ArrayList;

public class StatisticsList{
	private ArrayList list = new ArrayList();

	public ArrayList getList() {
		return list;
	}
	
	public StatisticsItem add(String name, long value, float fvalue, String parameter){
		StatisticsItem item = new StatisticsItem(name, value, fvalue, parameter);
		return this.add(item);
	}

	public StatisticsItem add(StatisticsItem item){
		list.add(item);
		return item;
	}
	
	
	
	public int size(){
		if( list!=null ){
			return list.size();
		}
		else{
			return 0;
		}
	}
	
	
	
	public StatisticsItem get(int i) throws Exception{
		if (list==null || list.size()<1)
			throw new Exception("List is empty");
		return (StatisticsItem)list.get(i);
	}
	
	public long getMaxValue() throws Exception{
		long max = Long.MIN_VALUE;
		if(list.isEmpty())return 0;
		else for (int i=0; i<list.size(); i++){
			if(((StatisticsItem)list.get(i)).getValue()>max){
				max=((StatisticsItem)list.get(i)).getValue();
			}
		}
		return max;
	}
	
	public float getMaxFValue() throws Exception{
		float max = Float.MIN_VALUE;
		if(list.isEmpty())return 0;
		else for (int i=0; i<list.size(); i++){
			if(((StatisticsItem)list.get(i)).getFvalue()>max){
				max=((StatisticsItem)list.get(i)).getFvalue();
			}
		}
		return max;
	}
	
	
	
	
}
