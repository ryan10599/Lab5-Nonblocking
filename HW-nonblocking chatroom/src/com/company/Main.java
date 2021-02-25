package com.company;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class Main {

    private static String cmd = new String();

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
                SelectionKey key = it.next(); // block
                it.remove();

                if(key.isAcceptable()){
                    ServerSocketChannel ch = (ServerSocketChannel) key.channel();
                    SocketChannel clientch = ch.accept();
                    clientch.configureBlocking(false);
                    clientch.register(selector, SelectionKey.OP_READ);
                }

                if(key.isReadable()){
                    System.out.println("Cc");
                    SocketChannel ch = (SocketChannel) key.channel();
                    ByteBuffer buf = ByteBuffer.allocate(20);
                    ch.read(buf);
                    buf.flip();
                    cmd = new String(buf.array());
                    ch.register(selector, SelectionKey.OP_WRITE);
                }

                if(key.isWritable()){
                    SocketChannel ch = (SocketChannel) key.channel();
                    ByteBuffer buf = ByteBuffer.allocate(20);
                    System.out.println(cmd);
                    buf.put(cmd.getBytes());
                    buf.flip();
                    ch.write(buf);
                    ch.register(selector, SelectionKey.OP_READ);
                }
            }
        }
    }
}
