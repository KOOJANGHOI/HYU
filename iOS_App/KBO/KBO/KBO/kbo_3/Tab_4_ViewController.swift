//
//  Tab_4_ViewController.swift
//  kbo_3
//
//  Created by 구장회 on 2017. 5. 5..
//  Copyright © 2017년 구장회. All rights reserved.
//

import UIKit

class Tab_4_ViewController: UIViewController {

    var goodsFlag:Int = 0
    @IBOutlet weak var ticketNum: UILabel!
    @IBOutlet weak var goodsImage: UIImageView!
    var str=""
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        switch goodsFlag {
        case 0:
            goodsImage.image = #imageLiteral(resourceName: "goods_1")
            goodsImage.layer.masksToBounds = true
        case 1: goodsImage.image = #imageLiteral(resourceName: "goods_2")
        case 2: goodsImage.image = #imageLiteral(resourceName: "goods_3")
        case 3: goodsImage.image = #imageLiteral(resourceName: "goods_4")
        case 4: goodsImage.image = #imageLiteral(resourceName: "goods_5")
        default: goodsImage.image = nil
        }
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        let deviceID = UIDevice.current.identifierForVendor!.uuidString
        getRequest(params: ["username" : deviceID], urlstr: urlStr_autheticate)

    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
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
            
            guard let data = data, error == nil else { return }
            
            self.str = NSString(data: data, encoding: String.Encoding.utf8.rawValue)! as String
            print((self.str as String) + " test ")
            self.ticketNum.text = self.str
  
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
