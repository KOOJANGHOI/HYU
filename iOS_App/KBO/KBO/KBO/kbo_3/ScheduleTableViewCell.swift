//
//  ScheduleTableViewCell.swift
//  kbo_3
//
//  Created by 구장회 on 2017. 5. 6..
//  Copyright © 2017년 구장회. All rights reserved.
//

import UIKit

class ScheduleTableViewCell: UITableViewCell {

    // 슬라이더 제어 변수
    var value:Int = 0
    
    @IBOutlet weak var GameTime: UILabel!       // 경기시간
    
    //원정팀 정보
    @IBOutlet weak var AwayTeamName: UILabel!
    @IBOutlet weak var AwayTeamScore: UILabel!
    @IBOutlet weak var AwayTeamLogo: UIImageView!
    
    //홈팀 정보
    @IBOutlet weak var HomeTeamScore: UILabel!
    @IBOutlet weak var HomeTeamName: UILabel!
    @IBOutlet weak var HomeTeamLogo: UIImageView!
    
    @IBOutlet weak var GamePlace: UILabel!      // 경기 장소
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
        
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
