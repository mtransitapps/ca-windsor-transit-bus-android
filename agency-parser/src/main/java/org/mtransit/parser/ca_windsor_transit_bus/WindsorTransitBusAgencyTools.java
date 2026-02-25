package org.mtransit.parser.ca_windsor_transit_bus;

import org.jetbrains.annotations.NotNull;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MDirection;

import java.util.Arrays;
import java.util.List;

public class WindsorTransitBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new WindsorTransitBusAgencyTools().start(args);
	}

	@NotNull
	@Override
	public List<Integer> getDirectionTypes() {
		return Arrays.asList(
				MDirection.HEADSIGN_TYPE_DIRECTION,
				MDirection.HEADSIGN_TYPE_STRING
		);
	}

	@NotNull
	@Override
	public String getStopCode(@NotNull GStop gStop) {
		if (!gStop.getStopCode().startsWith("77")) {
			return "77" + gStop.getStopCode(); // used by StrategicMapping API
		}
		return super.getStopCode(gStop); // used by StrategicMapping API
	}
}