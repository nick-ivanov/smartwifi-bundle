pragma solidity ^0.4.0;

contract RouterPlusPlus {
    bytes32 hash;
    address client_address;
    uint price = 20000000000000;
    uint timestamp;

    function setHash(bytes32 x) public {
        hash = x;
        timestamp = block.timestamp;
    }

    function getHash() public view returns (bytes32) {
        return hash;
    }

    function setAddress(address x) public {
        client_address = x;
    }

    function getAddress() public view returns (address) {
        return client_address;
    }

    function verify(bytes32 s, uint k) public view returns (bool) {
        return nest(s, k) == hash;
    }

    function nest(bytes32 s, uint k) public view returns (bytes32) {
        bytes32 roundHash = keccak256(s);

        for(uint i = 0; i < k; i++) {
            roundHash = keccak256(roundHash);
        }

        return roundHash;
    }

    function pay(bytes32 s, uint k) public payable {
        if(verify(s, k) && isRouter()) {
            msg.sender.transfer(k*price);
            client_address.transfer((60-k)*price);
        }
    }

    function refund(bytes32 s) public payable {
        if(verify(s, 59) && getTimeElapsed() > 42) {
            client_address.transfer(60*price);
        }
    }

    function ynEnoughBalance() public view returns (bool) {
        return address(this).balance >= (60 * price);
    }

    function contractBalance() public view returns (uint) {
        return address(this).balance;
    }

    function getTimeStamp() public view returns (uint) {
        return timestamp;
    }

    function getTimeElapsed() public view returns (uint) {
        return block.timestamp - timestamp;
    }

    function getSenderAddress() public payable returns (address) {
        return msg.sender;
    }

    function isRouter() public view returns (bool) {
        return (msg.sender == 0x786813FbE16DB8B2c974849B830510060a5402b0);
    }

    function () public payable {}
}
