//
//  ApplyViewController.swift
//  kbo_3
//
//  Created by 구장회 on 2017. 6. 6..
//  Copyright © 2017년 구장회. All rights reserved.
//

import UIKit

class ApplyViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func applyYes(_ sender: Any) {
        /* 응모시 처리부분 */
        
        let deviceID = UIDevice.current.identifierForVendor!.uuidString
        let entry = URL(string: urlStr_entry+deviceID+"&number=1")!
        
        let task = URLSession.shared.dataTask(with: entry as URL) { data, response, error in
            
            guard let data = data, error == nil else { return }
            
            let str = NSString(data: data, encoding: String.Encoding.utf8.rawValue)!
            print((str as String) + " test ")
            ticketback = str as String
        }
        
        task.resume()
        
        ticketNumber=ticketNumber-1
        print(ticketNumber)
        
        dismiss(animated: true, completion: nil)
    }
    
    @IBAction func applyNo(_ sender: Any) {
        dismiss(animated: true, completion: nil)
    }

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
