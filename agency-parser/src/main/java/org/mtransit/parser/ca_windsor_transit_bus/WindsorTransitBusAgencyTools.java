package org.mtransit.parser.ca_windsor_transit_bus;

import static org.mtransit.commons.StringUtils.EMPTY;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GRoute;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.gtfs.data.GTrip;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MDirection;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

// https://opendata.citywindsor.ca/Opendata/Details/218
// https://opendata.citywindsor.ca/Uploads/google_transit.zip
// https://windsor.mapstrat.com/current/
// https://windsor.mapstrat.com/current/google_transit.zip
public class WindsorTransitBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new WindsorTransitBusAgencyTools().start(args);
	}

	@Nullable
	@Override
	public List<Locale> getSupportedLanguages() {
		return LANG_EN;
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String getAgencyName() {
		return "Transit Windsor";
	}

	@Override
	public boolean excludeTrip(@NotNull GTrip gTrip) {
		if ("notinservice".equalsIgnoreCase(gTrip.getTripHeadsign())
				|| "not in service".equalsIgnoreCase(gTrip.getTripHeadsign())) {
			return true; // EXCLUDE
		}
		return super.excludeTrip(gTrip);
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return false;  // route_id used by GTFS-RT
	}

	@NotNull
	@Override
	public String getRouteShortName(@NotNull GRoute gRoute) {
		return super.getRouteShortName(gRoute); // used by Real-Time API
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.toLowerCaseUpperCaseWords(getFirstLanguageNN(), routeLongName);
		return CleanUtils.cleanLabel(routeLongName);
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	private static final String AGENCY_COLOR_BLUE = "009AD6"; // BLUE (from web site logo)
	private static final String AGENCY_COLOR = AGENCY_COLOR_BLUE;

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@Nullable
	@Override
	public String provideMissingRouteColor(@NotNull GRoute gRoute) {
		switch (gRoute.getRouteShortName()) {
		// @formatter:off
		case "1A": return "B50C43";
		case "1C": return "4E1E18";
		case "2": return "F68312";
		case "2222": return "ED1248";
		case "3": return "FEDF3F"; // "FFF44C";
		case "3W": return "CE910E";
		case "4": return "65C1EF";
		case "5": return "222771";
		case "6": return "FBC1A0";
		case "7": return "184A31";
		case "8": return "87CF32";
		case "10": return "F0319A";
		case "14": return "A67AC4";
		case "25": return "163A79";
		case "42": return "8000FF";
		// @formatter:on
		}
		throw new MTLog.Fatal("Unexpected route color %s!", gRoute);
	}

	private static final long ROUTE_ID_0 = 15L;

	@Override
	public boolean allowNonDescriptiveHeadSigns(long routeId) {
		if (routeId == ROUTE_ID_0 + 13L) { // 10
			return true; // because 2 direction_id w/ same head-sign & last stop
		}
		return super.allowNonDescriptiveHeadSigns(routeId);
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String cleanDirectionHeadsign(boolean fromStopName, @NotNull String directionHeadSign) {
		directionHeadSign = super.cleanDirectionHeadsign(fromStopName, directionHeadSign);
		if (fromStopName) {
			directionHeadSign = WINDSOR_TT_.matcher(directionHeadSign).replaceAll(WINDSOR_TT_REPLACEMENT);
		}
		return directionHeadSign;
	}

	@NotNull
	@Override
	public List<Integer> getDirectionTypes() {
		return Arrays.asList(
				MDirection.HEADSIGN_TYPE_DIRECTION,
				MDirection.HEADSIGN_TYPE_STRING
		);
	}

	private static final Pattern HDGH_ = CleanUtils.cleanWords("hotel dieu grace healthcare");
	private static final String HDGH_REPLACEMENT = CleanUtils.cleanWordsReplacement("HDGH");

	private static final Pattern ALL_ENTRANCE_ = CleanUtils.cleanWords("rear entrance", "front entrance");

	private static final Pattern WINDSOR_TT_ = CleanUtils.cleanWords(
			"transit windsor terminal", "transit windsor term",
			"transit terminal", "transit term"
	);
	private static final String WINDSOR_TT_REPLACEMENT = CleanUtils.cleanWordsReplacement("Windsor Transit Terminal");

	private static final Pattern RSN_BOUNDS_ = Pattern.compile("(^\\d+ (eastbound|westbound|northbound|southbound|e|w|n|s))", Pattern.CASE_INSENSITIVE);
	private static final String RSN_BOUNDS_REPLACEMENT = "$2";

	private static final Pattern RLN_RSN_ = Pattern.compile("(^[a-z]+[\\d]+$)", Pattern.CASE_INSENSITIVE);

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = RLN_RSN_.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = RSN_BOUNDS_.matcher(tripHeadsign).replaceAll(RSN_BOUNDS_REPLACEMENT);
		tripHeadsign = WINDSOR_TT_.matcher(tripHeadsign).replaceAll(WINDSOR_TT_REPLACEMENT);
		tripHeadsign = HDGH_.matcher(tripHeadsign).replaceAll(HDGH_REPLACEMENT);
		tripHeadsign = ALL_ENTRANCE_.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = CleanUtils.cleanBounds(tripHeadsign);
		tripHeadsign = CleanUtils.cleanNumbers(tripHeadsign);
		tripHeadsign = CleanUtils.cleanStreetTypes(tripHeadsign);
		return CleanUtils.cleanLabel(tripHeadsign);
	}

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = CleanUtils.CLEAN_AND.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AND_REPLACEMENT);
		gStopName = CleanUtils.CLEAN_AT.matcher(gStopName).replaceAll(CleanUtils.CLEAN_AT_REPLACEMENT);
		gStopName = CleanUtils.cleanBounds(gStopName);
		gStopName = CleanUtils.cleanNumbers(gStopName);
		gStopName = CleanUtils.cleanStreetTypes(gStopName);
		return CleanUtils.cleanLabel(gStopName);
	}

	@Override
	public int getStopId(@NotNull GStop gStop) {
		return super.getStopId(gStop); // used by GTFS-RT
	}

	@NotNull
	@Override
	public String getStopCode(@NotNull GStop gStop) {
		if ("Sto125649".equals(gStop.getStopCode())) {
			return "77" + "1262"; // Wyandotte @ Metro
		}
		if (!gStop.getStopCode().startsWith("77")) {
			return "77" + gStop.getStopCode(); // used by StrategicMapping API
		}
		return super.getStopCode(gStop); // used by StrategicMapping API
	}
}
