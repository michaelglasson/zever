## zever

This set of Java programs reads the output of a ZeverSolar inverter and posts it to PVOutput. It reads the inverter data every minute and posts five minute average data to PVOutput. At the end of each day, it uploads the daily summary.

Scheduling is done with Thread.sleep() commands. Monitoring stops at 18:00 and resumes at 08:00. 
