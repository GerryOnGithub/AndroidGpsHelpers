package ca.gerry;

import android.location.Location;
import android.util.Log;
import java.lang.*;

public class DistanceCalculator
{
  private static final String TAG = "gerry.DC";
  private static final int TWO_MINUTES = 1000 * 60 * 2;

  private static int maxAge = 1000 * 60 * 30;
  private static double minDistance = 50.0;
  private static int accuracy = 250;

  /*
   * Update criteria used in method betterLocation.
   * @param minAgeMs minimum diff in time between two values, used to prevent too-frequent updates
   * @param maxAgeMs maximum diff in time between two values, used to insure a minimum frequency of updates
   * @param accuracyMeters if accuracy is worse than this then the candidate location may be ignored
   * @param minDistanceMeters distance between the two values must be greater than this to be considered
   */
  static public void updateBetterLocationCriteria(int maximumAgeMs, int accuracyMeters,
                                                  double minDistanceMeters) {
    maxAge = maximumAgeMs;
    minDistance = minDistanceMeters;
    accuracy = accuracyMeters;
  }

  /*
  * Determine if a newly reported location is 'better' than a previous value.
  * Better means a more accurate representation of the device's current position.
  * Based on https://developer.android.com/guide/topics/location/strategies.html
  *
  * @param currentBest the current best location
  * @param candidate value to be evaluated for a better location
  * @return true if candidate is better
  */
  static public boolean betterLocation(Location currentBest, Location candidate) {
    if (currentBest == null) {
      return true;
    }

    long timeDelta = candidate.getTime() - currentBest.getTime();
    boolean significantlyNewer = timeDelta > maxAge;
    boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
    boolean newer = timeDelta > 0;

    if (significantlyNewer) {
      return true;
    }
    if (isSignificantlyOlder) {
      return false;
    }

    int accuracyDelta = (int) (candidate.getAccuracy() - currentBest.getAccuracy());
    boolean lessAccurate = accuracyDelta > 0;
    boolean moreAccurate = accuracyDelta < 0;
    boolean significantlyLessAccurate = accuracyDelta > accuracy;
    boolean tooClose = distance(currentBest, candidate) < minDistance;
    boolean fromSameProvider = sameProvider(candidate.getProvider(), currentBest.getProvider());

    Log.d(TAG, "less accurate: " + lessAccurate + ", more accurate: " + moreAccurate +
               ", way less accurate: " + significantlyLessAccurate + ", distant: " + tooClose +
               ", same provider " + fromSameProvider);

    if (significantlyLessAccurate || tooClose) {
      return false;
    }

    if (moreAccurate ||
       (newer && !lessAccurate) ||
       (newer && fromSameProvider)) {
      return true;
    }
    return false;
  }

  /** Checks whether two providers are the same */
  static private boolean sameProvider(String p1, String p2) {
    if (p1 == null) {
      return p2 == null;
    }
    return p1.equals(p2);
  }

  static public double distance(Location a, Location b) {
    return distance(a.getLatitude(), a.getLongitude(), a.getAltitude(),
                    b.getLatitude(), b.getLongitude(), b.getAltitude());
  }

  /*
   * Calculate distance between two points in latitude and longitude taking into account height difference. Uses Haversine method as its base.
   * Based on http://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude-what-am-i-doi
   *
   * @param lat1
   * @param lon1
   * @param el1 Start altitude in meters, use 0.0 to leave out this calculation
   * @param lat2
   * @param lon2
   * @param el2 End altitude in meters, use 0.0 to leave out this calculation
   * @returns Distance in Meters
  */
  public static double distance(double lat1, double lon1, double el1, double lat2, double lon2, double el2) {
    final int R = 6371; // approx. radius of the earth

    Double latDistance = Math.toRadians(lat2 - lat1);
    Double lonDistance = Math.toRadians(lon2 - lon1);
    Double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
      + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
      * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
    Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = R * c * 1000; // convert to meters
    double height = el1 - el2;
    distance = Math.pow(distance, 2) + Math.pow(height, 2);
    return Math.sqrt(distance);
  }
}
