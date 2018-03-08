//
//  DataViewController.swift
//  iOS-app
//
//  Created by Jaison on 08/03/18.
//  Copyright © 2018 Hasura. All rights reserved.
//

import UIKit
import Alamofire

class DataViewController: UIViewController {
    
    @IBOutlet weak var nameTextfield: UITextField!
    @IBOutlet weak var genderTextfield: UITextField!
    @IBOutlet weak var educationTableView: UITableView!
    
    var educationArray: [[String: Any]] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        guard let _ = Hasura.getSavedAuthToken() else {
            self.showAlert(title: "Unauthorized", message: "Cannot fetch user details for an anonymous user. Login/Signup first")
            return
        }
        educationTableView.dataSource = self
        
        let headers: HTTPHeaders? = [
            "Content-Type": "application/json",
            "Authorization": "Bearer " + Hasura.getSavedAuthToken()!
        ]
        
        let requestParam: [String: Any]? = [
            "type": "select",
            "args": [
                "table": "user_details",
                "columns": [
                    "user_id",
                    "name",
                    "gender",
                    [
                        "name": "education",
                        "columns": [
                            "institution_name",
                            "degree",
                            "id",
                            "user_id"
                        ]
                    ]
                ]
            ]
        ]
        
        Alamofire.request(Hasura.URL.data.getURL() + "v1/query",
            method: .post,
            parameters: requestParam,
            encoding: JSONEncoding.default,
            headers: headers)
            .responseJSON { response in
                debugPrint(response)
                switch response.result {
                case .success(let value):
                    if let responseArray = value as? [[String: Any]] {
                        if responseArray.count > 0 {
                            let userDetails = responseArray[0]
                            self.nameTextfield.text = userDetails["name"] as? String
                            self.genderTextfield.text = userDetails["gender"] as? String
                            if let educationArray = userDetails["education"] as? [[String: Any]] {
                                self.educationArray = educationArray
                                self.educationTableView.reloadData()
                            }
                        } else {
                            self.showAlert(title: "No User Details", message: "This user has no entry made in the user_details table")
                        }
                    }
                    break
                case .failure(let error):
                    self.showAlert(title: "Failed to fetch data", message: error.localizedDescription)
                    break
                }
        }
        
    }

    @IBAction func onCancelButtonClicked(_ sender: UIBarButtonItem) {
        self.dismiss(animated: true, completion: nil)
    }
}

extension DataViewController: UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return educationArray.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "EducationCell")
        let education = educationArray[indexPath.row]
        cell?.textLabel?.text = education["institution_name"] as? String
        cell?.detailTextLabel?.text = education["degree"] as? String
        return cell!
    }
}
