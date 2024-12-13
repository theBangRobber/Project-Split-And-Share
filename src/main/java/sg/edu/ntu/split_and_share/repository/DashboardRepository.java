package sg.edu.ntu.split_and_share.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.edu.ntu.split_and_share.entity.Dashboard;

public interface DashboardRepository extends JpaRepository<Dashboard, Long> {
  Optional<Dashboard> findByUser_Username(String username);
}
