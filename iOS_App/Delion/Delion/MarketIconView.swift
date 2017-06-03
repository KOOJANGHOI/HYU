//
//  MainIconView.swift
//  Delion
//
//  Created by kwonnahyun on 2016. 10. 17..
//  Copyright © 2016년 kwonnahyun. All rights reserved.
//

import Foundation
import UIKit

class MarketIconView : UIViewController{
    
    // 배달 아이콘 부분화면
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    // 아이콘 클릭 시 그 값을 MarketList의 네비게이션 바로 전달
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let marketVC: MarketList = segue.destination as! MarketList
        marketVC.markettype = segue.identifier!
    }

}
