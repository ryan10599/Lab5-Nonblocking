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

    private static ArrayList<SocketChannel> keyArray = new ArrayList<>();
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


                if(key.isAcceptable()){
                    ServerSocketChannel ch = (ServerSocketChannel) key.channel();
                    SocketChannel clientch = ch.accept();
                    clientch.configureBlocking(false);
                    clientch.register(selector, SelectionKey.OP_READ);
                    keyArray.add(clientch);
                    //System.out.println(keyArray.size());
                }

                if(key.isReadable()){
                    SocketChannel ch = (SocketChannel) key.channel();
                    ByteBuffer buf = ByteBuffer.allocate(1000);
                    //ch.configureBlocking(false);
                    buf.clear();
                    ch.read(buf);
                    buf.flip();
                    cmd = new String(buf.array());
                    System.out.println(cmd);
                    ch.register(selector, SelectionKey.OP_WRITE);
                }

                if(key.isWritable()){
                    //SocketChannel ch = (SocketChannel) key.channel();
//                    ByteBuffer buf = ByteBuffer.allocate(1000);
//                    System.out.println(cmd);
//                    buf.put(("S: "+cmd).getBytes());
//                    buf.flip();
//                    ch.write(buf);
//                    ch.register(selector, SelectionKey.OP_READ);
//                    System.out.println("Check");
//                    for(SocketChannel chs: keyArray){
//                        SocketChannel c = chs;
//                        ByteBuffer buf = ByteBuffer.allocate(3000);
//                        buf.put(("S: "+cmd).getBytes());
//                        buf.flip();
//                        c.write(buf);
//                        c.register(selector, SelectionKey.OP_READ);
//                    }
                    for(int i = 0; i < keyArray.size() ; i++){
                        SocketChannel c = keyArray.get(i);

                            ByteBuffer buf = ByteBuffer.allocate(3000);
                            buf.put(("S: " + cmd).getBytes());
                            buf.flip();
                            c.write(buf);
                            c.register(selector, SelectionKey.OP_READ);

                    }

                }
                it.remove();
            }
        }
    }
}
