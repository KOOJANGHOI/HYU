//
//  MoreViewController.swift
//  Delion
//
//  Created by kwonnahyun on 2016. 10. 17..
//  Copyright © 2016년 kwonnahyun. All rights reserved.
//

import Foundation
import UIKit
import MessageUI

class MoreViewController : UIViewController, MFMailComposeViewControllerDelegate{
    
    // 더보기 화면
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "더보기"
    }
    
    func messageComposeViewController(controller: MFMessageComposeViewController, didFinishWithresult: MessageComposeResult){
        self.presentingViewController?.dismiss(animated: true, completion: nil)
       // self.dismiss(animated: true, completion: nil)
    }
    
    // 메세지로 문의하기
    @IBAction func MessageButtonClicked(sender: AnyObject) {
        if MFMessageComposeViewController.canSendText(){
            let controller = MFMessageComposeViewController()
            controller.body = "문의사항을 입력해주세요."
            controller.recipients = ["01026243012"]
            controller.messageComposeDelegate = self as? MFMessageComposeViewControllerDelegate
            self.present(controller, animated: true, completion: nil)
        }
    }
    
   // 페이스북으로 문의하기
    @IBAction func FackbookButtonClicked(sender: AnyObject) {
        let forifURL = NSURL(string: "https://www.facebook.com/hforif")
        if #available(iOS 10.0, *) {
            UIApplication.shared.open(forifURL as! URL, options: [:])
        } else {
            UIApplication.shared.openURL(forifURL as! URL)
        }
    }
    
}
