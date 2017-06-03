//
//  LifeIconView.swift
//  Delion
//
//  Created by kwonnahyun on 2016. 10. 18..
//  Copyright © 2016년 kwonnahyun. All rights reserved.
//

import Foundation
import UIKit

class LifeIconView : UIViewController{
    
    // 생활정보 아이콘 부분화면
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    // 아이콘 클릭 시 그 값을 MarketList의 네비게이션 바로 전달
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let lifeVC: LifeList = segue.destination as! LifeList
        lifeVC.lifetype = segue.identifier!
    }
    
}
