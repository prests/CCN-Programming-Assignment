For part A I created a simple method of pinging an IP address 5 times using UDP packets. The client creates a blank 56 byte packet and sends that two the IP address and port provided by the user. The time after sending to when the response from the server is received is timed. The client has a 1sec timeout with a try catch function that goes into the catch when the timeout is reached and doesn't increment the sequence number. The server file was written by the professor but essential it receives the packet and has two variables. One variable is the percent chance that a packet is "lost" and the other is a fake average delay where it causes the main thread to sleep for that amount of time. Here are some tests and their output results

Test 1 (Packet Loss: 0% Delay: 0ms):

    Server Output:
    Received from 127.0.0.1:
    Reply sent.
    Received from 127.0.0.1:
    Reply sent.
    Received from 127.0.0.1:
    Reply sent.
    Received from 127.0.0.1:
    Reply sent.
    Received from 127.0.0.1:
    Reply sent.


    Client Output:
    PING 127.0.0.1 0 1
    PING 127.0.0.1 1 0
    PING 127.0.0.1 2 1
    PING 127.0.0.1 3 0
    PING 127.0.0.1 4 0


Test 2 (Packet Loss: 20% Delay: 100ms):

    Server Output:
    Received from 127.0.0.1:
    Reply sent.
    Received from 127.0.0.1:
    Reply sent.
    Received from 127.0.0.1:
    Reply sent.
    Received from 127.0.0.1:
    Reply not sent.
    Received from 127.0.0.1:
    Reply sent.
    Received from 127.0.0.1:
    Reply sent.

    Client Output:
    PING 127.0.0.1 0 158
    PING 127.0.0.1 1 169
    PING 127.0.0.1 2 58
    PING 127.0.0.1 3 LOST
    PING 127.0.0.1 3 152
    PING 127.0.0.1 4 29