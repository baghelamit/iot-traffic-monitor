//Create keyspace
CREATE KEYSPACE IF NOT EXISTS TrafficKeySpace WITH replication = {'class':'SimpleStrategy', 'replication_factor':1};

// Drop existing tables
DROP TABLE IF EXISTS TrafficKeySpace.Total_Traffic;
DROP TABLE IF EXISTS TrafficKeySpace.Window_Traffic;
DROP TABLE IF EXISTS TrafficKeySpace.Poi_Traffic;

//Create table
CREATE TABLE IF NOT EXISTS TrafficKeySpace.Total_Traffic (routeId text ,  vehicleType text,  totalCount bigint, timeStamp timestamp,recordDate text,PRIMARY KEY (routeId,recordDate,vehicleType));
CREATE TABLE IF NOT EXISTS TrafficKeySpace.Window_Traffic (routeId text , vehicleType text,  totalCount bigint, timeStamp timestamp,recordDate text,PRIMARY KEY (routeId,recordDate,vehicleType));
CREATE TABLE IF NOT EXISTS TrafficKeySpace.Poi_Traffic(vehicleid text , vehicletype text , distance bigint,  timeStamp timestamp,PRIMARY KEY (vehicleid));

