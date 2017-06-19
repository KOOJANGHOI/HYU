//
//  ViewController.swift
//  NSURLExample
//
//  Created by KIMTAEHO on 2017. 5. 19..
//  Copyright © 2017년 KIMTAEHO. All rights reserved.
//

import UIKit

struct SongInfo{
    var title:String?
    var image:String?
}



class ViewController: UIViewController {

    //let urlStr = "http://lorempixel.com/800/600/cats/"
    //let urlStr = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=25/json"
    var songs = [SongInfo]()
    
    var queue : OperationQueue!
    var str:String = ""

    
   // @IBOutlet weak var TextView: UITextView!
    @IBOutlet weak var imageView: UIImageView!
    
    override func viewDidLoad() {
        
        super.viewDidLoad()
    /*
        // Do any additional setup after loading the view, typically from a nib.
        queue = OperationQueue()
        queue.addOperation {
            let url = URL(string: self.urlStr)!
            let data = try! Data(contentsOf: url)
            let image = UIImage(data: data)
            OperationQueue.main.addOperation {
                self.imageView.image = image
            }
        }
      */
    
            //   TextView.text=str
        
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    
}

