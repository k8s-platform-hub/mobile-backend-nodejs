//
//  AuthViewController.swift
//  iOS-app
//
//  Created by Jaison on 03/11/17.
//  Copyright © 2017 Hasura. All rights reserved.
//

import UIKit
import Alamofire

class AuthViewController: UIViewController {
        
    @IBOutlet weak var username: UITextField!
    @IBOutlet weak var password: UITextField!
        
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    @IBAction func onCancelButtonPressed(_ sender: UIBarButtonItem) {
        self.dismiss(animated: true, completion: nil)
    }
    
    func performAuth(isSignUp: Bool) {
        let form = isFormValid()
        let path = isSignUp ? "v1/signup" : "v1/login"
        let url = Hasura.URL.auth.getURL() + path
        if form.0 {
            Alamofire.request(
                url,
                method: HTTPMethod.post,
                parameters: [
                    "provider": "username",
                    "data": [
                        "username": username.text!,
                        "password": password.text!
                    ]
                ],
                encoding: JSONEncoding.default,
                headers: nil
                )
            .validate()
            .responseJSON { (response) in
                    switch response.result {
                    case .success(let value):
                        if let authResponse = value as? [String: Any] {
                            let authToken = authResponse["auth_token"]! as! String
                            Hasura.saveAuthToken(authToken: authToken)
                            let message = "AuthToken: \(authToken)" 
                            self.showAlert(title: "Authentication Successful", message: message)
                        }
                        break
                    case .failure(let error):
                        self.showAlert(title: "Authentication Failed", message: error.localizedDescription)
                        break
                    }
            }
        } else {
            self.showAlert(title: "Form invalid", message: form.1!)
        }
    }
    
    @IBAction func onSignUpButtonClicked(_ sender: UIButton) {
        self.performAuth(isSignUp: true)
    }
    
    @IBAction func onSignInButtonClicked(_ sender: UIButton) {
        self.performAuth(isSignUp: false)
    }
    
    func isFormValid() -> (Bool, String?) {
        if username.text!.isEmpty {
            return (false, "Username is a mandatory field")
        }
        if (password.text!.isEmpty) {
            return (false, "Password is a mandatory field")
        }
        return (true, nil)
    }
    
}
