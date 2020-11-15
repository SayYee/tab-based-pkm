    function send() {
        var message = document.getElementById('text').value;
        document.getElementById('message').innerHTML += message + '<br/>';
        window.app.doIt(message);
    }