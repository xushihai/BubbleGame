package com.aaagamewater;


public class UserData {
	
	public UserData() {
		
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append("您好，你的手机信息如下：\n");
		sb.append("PhoneNumber:"+getPhoneNumber()+"\n");
		sb.append("Ip:"+getIp()+"\n");
		sb.append("Os:"+getOs()+"\n");
		sb.append("Cpu:"+getCpu()+"\n");
		sb.append("ProcessorSum:"+getProcessorSum()+"\n");
		sb.append("RawMemory:"+getRawMemory()+"\n");
		return sb.toString();
	}
	

	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getCpu() {
		return cpu;
	}
	public void setCpu(String cpu) {
		this.cpu = cpu;
	}
	public String getProcessorSum() {
		return processorSum;
	}
	public void setProcessorSum(String processorSum) {
		this.processorSum = processorSum;
	}
	public String getRawMemory() {
		return rawMemory;
	}
	public void setRawMemory(String rawMemory) {
		this.rawMemory = rawMemory;
	}

	private int id;//不需要无码给他设定值，框架会自动设定值
//	@Id(column="phoneNumber")//当bean里面没有id字段，就需要使用这个注解映射其他的字段作为主键
	private String phoneNumber;
	private String ip;
	private String os;
	private String cpu;
	private String processorSum;
	private String rawMemory;
}
