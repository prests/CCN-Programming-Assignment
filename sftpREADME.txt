For the second part of this assignment I created a unidirectional file transfer from a client to a server. This was a little more complicated than part A and what I did to accomplish this was as followed.

I read in a text file in the client java file, which was then created into a byte array. That byte array was then sectioned up into groups of 512 bytes and then stored in a 2D array of byte arrays. These sections would make up the packets and were then put into DatagramPackets and sent and followed by another one byte packet which was the sequence number. The packet and sequence number would be received by the server and just like part A decide if a response should be sent. The difference from part A to part B here is that the response would be the client's ack number. The client also had a limit of 6 retransmissions with a timeout of 1sec. The received packets by the server would be created back to strings and stored in an output text file. Once all the packets were received a bitwise comparison of the two textfiles are done to see if they're equal and the client times the whole exchange not just to send one packet. Test I recorded are below with graphs attached to pdf provided.


Fixed AVERAGE_DELAY 100ms
Test 1 (LOSS_RATE 0%):

    Client:
    sFTP: file sent successfully to 127.0.0.1 in 10 secs

Test 2 (LOSS_RATE 5%):

    Client:
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    sFTP: file sent successfully to 127.0.0.1 in 14 secs

Test 3 (LOSS_RATE 10%):

    Client:
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    sFTP: file sent successfully to 127.0.0.1 in 26 secs

Test 4 (LOSS_RATE 15%):

    Client:
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    sFTP: file sent successfully to 127.0.0.1 in 25 secs




Fixed LOSS_RATE 10%
Test 1 (AVERAGE_DELAY 50ms):

    Client:
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    sFTP: file sent successfully to 127.0.0.1 in 11 secs

Test 2 (AVERAGE_DELAY 100ms):

    Client:
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    sFTP: file sent successfully to 127.0.0.1 in 20 secs

Test 3 (AVERAGE_DELAY 200ms):

    Client:
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    sFTP: file sent successfully to 127.0.0.1 in 29 secs

Test 4 (AVERAGE_DELAY 400ms):

    Client:
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    Packet lost
    sFTP: file sent successfully to 127.0.0.1 in 45 secs