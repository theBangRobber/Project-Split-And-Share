package sg.edu.ntu.split_and_share.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import sg.edu.ntu.split_and_share.repository.DashboardRepository;
import sg.edu.ntu.split_and_share.repository.UserRepository;



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
        // //Arrange - got one parameter which is String username
        // String username = "Mmmanyuu";
        // Dashboard mockDashboard = new Dashboard();
        // mockDashboard.setExpenses(List.of(
        //     new Expense(1L, "Food", 100.0, username, username, mockDashboard, null),
            
        // ));

        
        // List<Expense> expenses = Arrays.asList(
        //     new Expense(1L,"Expense 1", 100.0, username, username, null, null),
            
        // )

    }

    @Test
    public void testCalculateTotalSum_Unsuccessful_DashboardNotFound (){

    //   User existingUser = User.builder().id(1L).username("Mmanyuu").password("123456789").name("Manyu").dashboard(null).build();

    //   when(dashboardRepository.findByUser_Username("Mmanyuu")).thenReturn(Optional.of(existingUser));

    //     Exception exception = assertThrows(DashboardNotFoundException.class, ()->dashboardService.calculateTotalSum())
    // }
}
}