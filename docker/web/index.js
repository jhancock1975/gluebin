document.addEventListener('DOMContentLoaded', function() {
  console.log('DOM content has been loaded and parsed');
});

function getTimestamp() {
  let date = new Date();

  let year = date.getFullYear();
  let month = ('0' + (date.getMonth() + 1)).slice(-2);
  let day = ('0' + date.getDate()).slice(-2);

  let hour = ('0' + date.getHours()).slice(-2);
  let minute = ('0' + date.getMinutes()).slice(-2);
  let second = ('0' + date.getSeconds()).slice(-2);

  let millis = ('00' + date.getMilliseconds()).slice(-3);

  let timestamp = `${year}-${month}-${day}T${hour}:${minute}:${second}.${millis}`;

  return timestamp;
}

const postPaste= async (textEltId) => {
  pasteObj = {};
  pasteText = document.getElementById(textEltId).value;
  pasteObj['pasteText'] = pasteText;
  console.log('pasteObj', pasteObj)
  const response = await fetch('http://localhost:19999/pastes', {
    method: 'POST',
    body: JSON.stringify(pasteObj),
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

const getPaste = async (textEltId) => {
  pasteObj = {};
  pasteId = document.getElementById(textEltId).value;
  pasteObj['shortLink'] = pasteId;
  pasteObj['pastePath'] = pasteId;
  console.log('pasteObj', pasteObj)
  const response = await fetch('http://localhost:19999/pastes/' + pasteId, {
    method: 'GET',
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
