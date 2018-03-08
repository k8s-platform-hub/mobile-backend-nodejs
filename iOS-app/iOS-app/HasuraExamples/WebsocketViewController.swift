//
//  WebsocketViewController.swift
//  iOS-app
//
//  Created by Jaison on 08/03/18.
//  Copyright © 2018 Hasura. All rights reserved.
//

import UIKit
import SocketIO

class WebsocketViewController: UIViewController {

    @IBOutlet weak var textfield: UITextField!
    @IBOutlet weak var messageTableView: UITableView!
    
    static let SOCKET_EVENT = "message"
    
    var messageData: [String] = []
    var keyboardActive = false
    
    let manager = SocketManager(socketURL: URL(string: Hasura.URL.custom.getURL())!, config: [.log(true)])
    
    var socket: SocketIOClient!
        
    override func viewDidLoad() {
        super.viewDidLoad()
        textfield.delegate = self
        
        messageTableView.dataSource = self
        socket = manager.defaultSocket
        socket.on(clientEvent: .connect) {data, ack in
            print("socket connected")
        }
        socket.on(WebsocketViewController.SOCKET_EVENT) {data, ack in
            print("message")
            self.messageData.append(data[0] as! String)
            self.messageTableView.reloadData()
        }
        socket.on(clientEvent: .disconnect) { (data, ack) in
            print("socket disconnected")
        }
        socket.connect()
    }

    
    @IBAction func onSendButtonClicked(_ sender: UIButton) {
        guard let message = textfield.text else {
            return
        }
        socket.emit(WebsocketViewController.SOCKET_EVENT, message)
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        socket.disconnect()
    }
}

extension WebsocketViewController: UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return messageData.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "MessageCell")
        cell?.textLabel?.text = messageData[indexPath.row]
        return cell!
    }
}

extension WebsocketViewController: UITextFieldDelegate {
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        self.view.endEditing(true)
        return false
    }
}


