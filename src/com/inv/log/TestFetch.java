package com.inv.log;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;
import com.maxmind.geoip.regionName;
import com.maxmind.geoip.timeZone;

public class TestFetch {
	public static void main(String[] args) throws IOException {
		LookupService cl = new LookupService("./GeoIPCity.dat",
				LookupService.GEOIP_MEMORY_CACHE);
		Location l2 = cl.getLocation("74.39.74.158");
		//Location l2 = cl.getLocation(args[0]);
		System.out.println("countryCode: "
				+ l2.countryCode
				+ "\n countryName: "
				+ l2.countryName
				+ "\n region: "
				+ l2.region
				+ "\n regionName: "
				+ regionName.regionNameByCode(l2.countryCode, l2.region)
				+ "\n city: "
				+ l2.city
				+ "\n postalCode: "
				+ l2.postalCode
				+ "\n latitude: "
				+ l2.latitude
				+ "\n longitude: "
				+ l2.longitude

				+ "\n metro code: "
				+ l2.metro_code
				+ "\n area code: "
				+ l2.area_code
				+ "\n timezone: "
				+ timeZone
						.timeZoneByCountryAndRegion(l2.countryCode, l2.region) + "\n Location: ");
		cl.close();

	}
}
