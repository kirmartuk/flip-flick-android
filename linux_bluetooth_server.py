#!/usr/bin/env python3
"""PyBluez simple example rfcomm-server.py
Simple demonstration of a server application that uses RFCOMM sockets.
Author: Albert Huang <albert@csail.mit.edu>
$Id: rfcomm-server.py 518 2007-08-10 07:20:07Z albert $
"""
import bluetooth
import json

from Command import Command
from bluetoothRepository import BluetoothRepository


def main1():
    server_sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
    server_sock.bind(("", bluetooth.PORT_ANY))
    server_sock.listen(1)
    port = server_sock.getsockname()[1]
    uuid = "00001100-0000-1000-8000-00805f9b34fb"
    bluetooth.advertise_service(server_sock, "SampleServer", service_id=uuid,
                                service_classes=[uuid, bluetooth.SERIAL_PORT_CLASS],
                                profiles=[bluetooth.SERIAL_PORT_PROFILE],
                                # protocols=[bluetooth.OBEX_UUID]
                                )
    print("Waiting for connection on RFCOMM channel", port)

    client_sock, client_info = server_sock.accept()
    print("Accepted connection from", client_info)
    try:
        while True:
            data = client_sock.recv(1024)
            print("Received", data)
            try:
                x = json.loads(data, object_hook=lambda d: Command(**d))
                print(str(x.device))
                if x.command == "connect":
                    BluetoothRepository.connect_to_device(x.device)
                elif x.command == "disconnect":
                    BluetoothRepository.disconnect_device(x.device)
                elif x.command == "get_connected_devices":
                    client_sock.send(str(BluetoothRepository.get_connected_devices()))
                if not data:
                    break
            except Exception:
                print("error")
            print("Received", data.decode())
    except OSError:
        print("Disconnected.")
        client_sock.close()
        server_sock.close()
        print("All done.")
        main1()


if __name__ == '__main__':
    main1()
