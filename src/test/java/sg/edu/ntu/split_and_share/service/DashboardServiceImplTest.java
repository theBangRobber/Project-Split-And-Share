package sg.edu.ntu.split_and_share.service;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import sg.edu.ntu.split_and_share.entity.Expense;
import sg.edu.ntu.split_and_share.repository.DashboardRepository;
import sg.edu.ntu.split_and_share.repository.UserRepository;

import java.util.List;

@ExtendWith(MockitoExtension.class) 
public class DashboardServiceImplTest {

    @Mock
    private DashboardRepository dashboardRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    DashboardServiceImpl dashboardService;

    @Test
    public void testCalculateTotalSum_Successful (){
        //Arrange - got one parameter which is String username
        String username = "Mmanyuu";
        List<Expense> expenses = Arrays.asList(
            new Expense(1L,"Expense 1", 100.0, username, username, null, null),
            
        )

    }

    @Test
    public void testCalculateTotalSum_Unsuccessful_DashboardNotFound (){

    }
}
