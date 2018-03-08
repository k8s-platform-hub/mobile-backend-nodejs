//
//  +UIViewController.swift
//  iOS-app
//
//  Created by Jaison on 03/11/17.
//  Copyright © 2017 Hasura. All rights reserved.
//

import Foundation
import UIKit

extension UIViewController {
    
    func setNavigationBar(withTitle title: String, rightButton: UIBarButtonItem? = nil, leftButton: UIBarButtonItem? = nil) {
        let screenSize: CGRect = UIScreen.main.bounds
        let navBar = UINavigationBar(frame: CGRect(x: 0, y: 0, width: screenSize.width, height: 60))
        let navItem = UINavigationItem(title: title)
        
        if let rightButton = rightButton {
            navItem.rightBarButtonItem = rightButton
        }
        if let leftButton = leftButton {
            navItem.leftBarButtonItem = leftButton
        }
        navBar.setItems([navItem], animated: false)
        self.view.addSubview(navBar)
    }

    
    func showAlert(title: String, message: String) {
        let alertVC = UIAlertController(title: title, message: message, preferredStyle: UIAlertControllerStyle.alert)
        let dismissAction = UIAlertAction(title: "Dismiss", style: UIAlertActionStyle.cancel) { (action) in
            alertVC.dismiss(animated: true, completion: nil)
        }
        alertVC.addAction(dismissAction)
        self.present(alertVC, animated: true, completion: nil)
    }
}
