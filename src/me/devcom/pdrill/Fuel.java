package me.devcom.pdrill;

public class Fuel {
	public Integer block_id;
	public Integer drillAirSpeed;
	public Integer drillBlockSpeed;
	public Integer fuelConsumptionBlockCount;
	public Integer fuelConsumptionFuelCount;
	public String configName;
	
	public Fuel(Integer bid, Integer das, Integer dbs, Integer bc, Integer fc, String name){
		block_id = bid;
		drillAirSpeed = das;
		drillBlockSpeed = dbs;
		fuelConsumptionBlockCount = bc;
		fuelConsumptionFuelCount = fc;
		configName = name;
	}
}
