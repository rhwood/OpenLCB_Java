package org.openlcb.implementations;

import org.openlcb.*;

import org.junit.*;

/**
 * @author  Bob Jacobsen   Copyright 2009
 */
public class DatagramTransmitterTest {
    
    NodeID hereID = new NodeID(new byte[]{1,2,3,4,5,6});
    NodeID farID  = new NodeID(new byte[]{1,1,1,1,1,1});
    
    int[] data;

    java.util.ArrayList<Message> messagesReceived;
   
    @Test 
    public void testSendOK() {
        messagesReceived = new java.util.ArrayList<Message>();
        Connection testConnection = new AbstractConnection(){
            public void put(Message msg, Connection sender) {
                messagesReceived.add(msg);
            }
        };

        data = new int[32];

        DatagramTransmitter xmt = new DatagramTransmitter(
                                            hereID,farID,
                                            data,
                                            testConnection);
                                                    
        Assert.assertEquals("init messages", 1, messagesReceived.size());
        Assert.assertTrue(messagesReceived.get(0)
                           .equals(new DatagramMessage(hereID, farID, data)));

        // Accepted
        Message m = new DatagramAcknowledgedMessage(farID, hereID);
        messagesReceived = new java.util.ArrayList<Message>();

        xmt.put(m, null);

        Assert.assertEquals("1st messages", 0, messagesReceived.size());
        
    }
    
    @Test 
    public void testOneResendNeeded() {
        messagesReceived = new java.util.ArrayList<Message>();
        Connection testConnection = new AbstractConnection(){
            public void put(Message msg, Connection sender) {
                messagesReceived.add(msg);
            }
        };
        
        data = new int[32];
        
        DatagramTransmitter xmt = new DatagramTransmitter(
                                            hereID,farID,
                                            data,
                                            testConnection);
                                                    
        Assert.assertEquals("init messages", 1, messagesReceived.size());
        Assert.assertTrue(messagesReceived.get(0)
                           .equals(new DatagramMessage(hereID, farID, data)));
                           
        // Reject once
        Message m = new DatagramRejectedMessage(farID, hereID,0x021);
        messagesReceived = new java.util.ArrayList<Message>();

        xmt.put(m, null);

        Assert.assertEquals("1st messages", 1, messagesReceived.size());
        Assert.assertTrue(messagesReceived.get(0)
                           .equals(new DatagramMessage(hereID, farID, data)));

        // Accepted
        m = new DatagramAcknowledgedMessage(farID, hereID);
        messagesReceived = new java.util.ArrayList<Message>();

        xmt.put(m, null);

        Assert.assertEquals("2nd messages", 0, messagesReceived.size());
    }
    
}
