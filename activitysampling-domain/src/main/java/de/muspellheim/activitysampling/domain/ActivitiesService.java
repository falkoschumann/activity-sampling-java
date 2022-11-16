package de.muspellheim.activitysampling.domain;

public interface ActivitiesService {
  void logActivity(String description);

  RecentActivities selectRecentActivities();
}
