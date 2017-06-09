//
//  PredictViewController.swift
//  kbo_3
//
//  Created by 구장회 on 2017. 6. 6..
//  Copyright © 2017년 구장회. All rights reserved.
//

import UIKit

class PredictViewController: UIViewController {

    @IBOutlet weak var predictSegment: UISegmentedControl!
    var result:Int = 0
    var gameNum:Int = 10
    
    override func viewDidLoad() {
        super.viewDidLoad()
        print(gameNum)
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    @IBAction func predictSegmentClick(_ sender: Any) {
        
       
        if(predictSegment.selectedSegmentIndex == 0) // 원정팀 승
        {
            result = -1
          
        }
        else if(predictSegment.selectedSegmentIndex == 1) // 홈팀 승
        {
            result = 0
        }
        else if(predictSegment.selectedSegmentIndex == 2) // 홈팀 승
        {
            result = 1
        }
        
        let deviceID = UIDevice.current.identifierForVendor!.uuidString
    
        getRequest(params: ["username": deviceID, "game_id":String(gameNum), "result":String(result)], urlstr: urlStr_predict)

    }
    
    // 예측 버튼 Yes
    @IBAction func predictYes(_ sender: Any) {
        // 선택 후 이벤트
        print("yes")
        print(gameNum)
        dismiss(animated: true, completion: nil)
    }
    // 예측 버튼 No
    @IBAction func predictNo(_ sender: Any) {
        print("no")
        print(gameNum)
        dismiss(animated: true, completion: nil)
    }
    
    func getRequest(params: [String:String], urlstr:String) {
        
        let urlComp = NSURLComponents(string: urlstr)!
        
        var items = [URLQueryItem]()
        
        for (key,value) in params {
            items.append(URLQueryItem(name: key, value: value))
        }
        
        items = items.filter{!$0.name.isEmpty}
        
        if !items.isEmpty {
            urlComp.queryItems = items
        }
        
        var urlRequest = URLRequest(url: urlComp.url!)
        urlRequest.httpMethod = "GET"
        let config = URLSessionConfiguration.default
        let session = URLSession(configuration: config)
        
        let task = session.dataTask(with: urlRequest, completionHandler: { (data, response, error) in
        })
        task.resume()
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
