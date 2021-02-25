package com.company;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Main {

    public static void main(String[] args) throws Exception {
        Selector selector = Selector.open();

        ServerSocketChannel serverCh = ServerSocketChannel.open();
        serverCh.configureBlocking(false);
        serverCh.bind(new InetSocketAddress(9000));
        serverCh.register(selector, SelectionKey.OP_ACCEPT);

        System.out.println("listen from connections");
        while (true){
            selector.select(); //waitching events
            System.out.println("got some events");
            Set<SelectionKey> keys = selector.selectedKeys(); //list of events happen
            //do something with Keys
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()){
                SelectionKey key = it.next();
                it.remove();

                if(key.isAcceptable()){
                    ServerSocketChannel ch = (ServerSocketChannel) key.channel();
                    SocketChannel clientch = ch.accept();
                    clientch.configureBlocking(false);
                    clientch.register(selector, SelectionKey.OP_READ);
                }
//                if(key.isWritable()){
//                    //client ready for write
//                    SocketChannel ch = (SocketChannel) key.channel();
//                    ByteBuffer buf = ByteBuffer.allocate(20);
//                    String msg = String.format("Time:%d\n", System.currentTimeMillis());
//                    buf.put(msg.getBytes());
//                    buf.flip();
//                    ch.write(buf);
//                    Thread.sleep(1000);
//                }
                if(key.isReadable()){
                    System.out.println("Cc");
                    SocketChannel ch = (SocketChannel) key.channel();
                    ByteBuffer buf = ByteBuffer.allocate(20);
                    ch.read(buf);
                    buf.flip();
                    String cmd = new String(buf.array());
                    System.out.println(cmd);


                    buf.clear();
                    System.out.println();
                    String msg = String.format("Time:%d\n", System.currentTimeMillis());
                    System.out.println(msg);
                    buf.put(msg.getBytes());
                    buf.flip();
                    ch.write(buf);
                    ch.register(selector, SelectionKey.OP_READ);
                }
            }
        }
    }
}
