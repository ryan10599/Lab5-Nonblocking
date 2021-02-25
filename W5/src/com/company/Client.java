package com.company;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Client {
    public static void main(String[] args) throws Exception {
        Selector selector =Selector.open();

        SocketChannel clientch = SocketChannel.open();
        clientch.configureBlocking(false);
        clientch.register(selector, SelectionKey.OP_CONNECT);

        clientch.connect(new InetSocketAddress("127.0.0.1", 9000));

        while (true){
            selector.select();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> it = keys.iterator();
            while (it.hasNext()){
                SelectionKey key = it.next();
                it.remove();
                //check or do something with event
                if(key.isConnectable()){
                    SocketChannel ch = (SocketChannel) key.channel();
                    if(!ch.finishConnect()){
                        ch.close();
                        continue;
                    }
                    ch.configureBlocking(false);
                    ch.register(selector, SelectionKey.OP_WRITE | SelectionKey.OP_READ);
                }
                if(key.isWritable()){
                    //client ready for write
                    SocketChannel ch = (SocketChannel) key.channel();
                    ByteBuffer buf = ByteBuffer.allocate(20);
                    //String msg = String.format("Time:%d\n", System.currentTimeMillis());
                    String msg = "UPDATE\n";
                    buf.put(msg.getBytes());
                    buf.flip();
                    ch.write(buf);
                    ch.register(selector, SelectionKey.OP_READ);
                    Thread.sleep(1000);
                }
                if(key.isReadable()){
                   SocketChannel ch = (SocketChannel) key.channel();
                    ByteBuffer buf = ByteBuffer.allocate(20);
                    ch.read(buf);
                    buf.flip();
                    ch.register(selector, SelectionKey.OP_WRITE);
                    System.out.print(new String(buf.array()));
                }
            }
        }

    }
}
