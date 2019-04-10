#!/bin/bash

INTERFACE=enx00e04c680083

printf "Trying to disable the Internet for client with IP %s\n" "$1"


#sudo iptables -F
#
#sudo iptables -P INPUT ACCEPT
#sudo iptables -P INPUT ACCEPT
#sudo iptables -P FORWARD ACCEPT
#
#sudo iptables -I OUTPUT -s $1 -p tcp --dport 80 -j DROP
#sudo iptables -I OUTPUT -s $1 -p tcp --dport 443 -j DROP
#sudo iptables -I INPUT -s $1 -p tcp --dport 80 -j DROP
#sudo iptables -I INPUT -s $1 -p tcp --dport 443 -j DROP
#sudo iptables -I FORWARD -s $1 -p tcp --dport 80 -j DROP
#sudo iptables -I FORWARD -s $1 -p tcp --dport 443 -j DROP
#
#sudo iptables -I OUTPUT -s $1 -p tcp --sport 80 -j DROP
#sudo iptables -I OUTPUT -s $1 -p tcp --sport 443 -j DROP
#sudo iptables -I INPUT -s $1 -p tcp --sport 80 -j DROP
#sudo iptables -I INPUT -s $1 -p tcp --sport 443 -j DROP
#sudo iptables -I FORWARD -s $1 -p tcp --sport 80 -j DROP
#sudo iptables -I FORWARD -s $1 -p tcp --sport 443 -j DROP




# ORIGINAL:
#sudo iptables -F
#
#sudo iptables -P INPUT ACCEPT
#sudo iptables -P INPUT ACCEPT
#sudo iptables -P FORWARD ACCEPT
#
#sudo iptables -I OUTPUT -o $INTERFACE -p tcp --dport 80 -j DROP
#sudo iptables -I OUTPUT -o $INTERFACE -p tcp --dport 443 -j DROP
#sudo iptables -I INPUT -i $INTERFACE -p tcp --dport 80 -j DROP
#sudo iptables -I INPUT -i $INTERFACE -p tcp --dport 443 -j DROP
#sudo iptables -I FORWARD -i $INTERFACE -p tcp --dport 80 -j DROP
#sudo iptables -I FORWARD -i $INTERFACE -p tcp --dport 443 -j DROP
#
#sudo iptables -I OUTPUT -o $INTERFACE -p tcp --sport 80 -j DROP
#sudo iptables -I OUTPUT -o $INTERFACE -p tcp --sport 443 -j DROP
#sudo iptables -I INPUT -i $INTERFACE -p tcp --sport 80 -j DROP
#sudo iptables -I INPUT -i $INTERFACE -p tcp --sport 443 -j DROP
#sudo iptables -I FORWARD -i $INTERFACE -p tcp --sport 80 -j DROP
#sudo iptables -I FORWARD -i $INTERFACE -p tcp --sport 443 -j DROP

echo "Internet access disabled!"