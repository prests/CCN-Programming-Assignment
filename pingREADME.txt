



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


Test 1 (Packet Loss: 20% Delay: 100ms):

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