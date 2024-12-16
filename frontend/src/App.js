import React, { useEffect,useState} from 'react';
import axios from 'axios';

const App = () => {
  const [apiResponse, setApiResponse] = useState(null);
  const [error, setError] = useState(null); // State to store errors, if any

  useEffect(() => {
    const testConnection = async () => {
      // try {
        // const response = await axios.get('http://localhost:8080/api/user/allusers'); // Replace with your endpoint
        // console.log('Response from backend:', response.data);
      // } catch (error) {
        // console.error('Error connecting to backend:', error);
      // }

      try {
        const response = await axios.get('http://localhost:8080/api/user/allusers'); // Replace with your endpoint
        console.log('Response from backend:', response.data);
        setApiResponse(response.data); // Update the state with the API response
      } catch (error) {
        console.error('Error connecting to backend:', error);
        setError('Error fetching data'); // Set an error message if the request fails
      }
    };

    testConnection();
  }, []);
  return (
    <div>
      <h1>React and Backend Integration Test</h1>
      <p>Check the console for API responses.</p>

      {/* Display the API response or an error message */}
      <div>
        {error && <p style={{ color: 'red' }}>{error}</p>} {/* Show error message if there's an issue */}
        {apiResponse ? (
          <pre>{JSON.stringify(apiResponse, null, 2)}</pre> // Display response as a formatted JSON
        ) : (
          <p>Loading...</p> // Show loading text until the response is fetched
        )}
      </div>
    </div>
  );
};

export default App;
