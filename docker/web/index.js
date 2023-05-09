document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM content has been loaded and parsed');
});
    myBody = {
        shortUrlLink: "abc",
        expirationLengthInMinutes: 10,
        createdAt: "2023-04-30T00:00:00",
        pastePath: "/paste/path"
    };

    const userAction = async (textEltId) => {
      pasteText = document.getElementById(textEltId).value;
      myBody['pasteText'] = pasteText;
      console.log('myBody', myBody)
      const response = await fetch('http://localhost:19999/pastes', {
          method: 'POST',
          body: JSON.stringify(myBody),
          headers: {
            'Content-Type': 'application/json'
          }
    }).then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json(); // Parse the response as JSON
        })
        .then(data => {
            console.log('Success:', data); // Log the success response
        })
        .catch(error => {
            console.error('Error:', error); // Log any errors
        });
    }
/* comment */
