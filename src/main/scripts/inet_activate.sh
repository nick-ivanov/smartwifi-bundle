#!/bin/bash

INTERFACE=enx00e04c680083

printf "Trying to enable the Internet for client with IP %s\n" "$1"

sudo iptables -F

sudo iptables -P INPUT ACCEPT
sudo iptables -P OUTPUT ACCEPT
sudo iptables -P FORWARD ACCEPT


# ORIGINAL:
#sudo iptables -F
#
#sudo iptables -P INPUT ACCEPT
#sudo iptables -P OUTPUT ACCEPT
#sudo iptables -P FORWARD ACCEPT

echo "Internet access enabled!"
