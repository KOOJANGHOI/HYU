//
//  Tab_3_ViewController.swift
//  kbo_3
//
//  Created by 구장회 on 2017. 5. 5..
//  Copyright © 2017년 구장회. All rights reserved.
//

import UIKit

class Tab_3_ViewController: UIViewController, UITableViewDelegate {
    
    // 투수 컨테이너
    @IBOutlet weak var pitcherContainer: UIView!
    // 타자 컨테이너
    @IBOutlet weak var batterContainer: UIView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        
        // 초기화면 설정(투수 컨테이너 부터 보임)
        pitcherContainer.isHidden = false
        batterContainer.isHidden = true
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // 메인 세그먼트
    @IBOutlet weak var mainSegment: UISegmentedControl!
    
    // 메인 세그먼트 클릭시
    @IBAction func mainSegmentAction(_ sender: Any) {
        
        if(mainSegment.selectedSegmentIndex == 0) // 투수
        {
            pitcherContainer.isHidden = false
            batterContainer.isHidden = true
        }
        else if(mainSegment.selectedSegmentIndex == 1) // 타자
        {
            pitcherContainer.isHidden = true
            batterContainer.isHidden = false
        }
        
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
