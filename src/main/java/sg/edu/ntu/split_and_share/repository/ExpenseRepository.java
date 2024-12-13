package sg.edu.ntu.split_and_share.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sg.edu.ntu.split_and_share.entity.Expense;

public interface ExpenseRepository extends JpaRepository<Expense, Long> {

}
