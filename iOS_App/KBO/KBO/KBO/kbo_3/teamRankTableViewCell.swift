//
//  teamRankTableViewCell.swift
//  kbo_3
//
//  Created by 구장회 on 2017. 5. 6..
//  Copyright © 2017년 구장회. All rights reserved.
//

import UIKit

class teamRankTableViewCell: UITableViewCell {

    // 순위,팀명,경기수,숭,무,패,승률,로고
    @IBOutlet weak var TeamRank: UILabel!
    @IBOutlet weak var TeamName: UILabel!
    @IBOutlet weak var GameNum: UILabel!
    @IBOutlet weak var WinNum: UILabel!
    @IBOutlet weak var DrawNum: UILabel!
    @IBOutlet weak var LoseNum: UILabel!
    @IBOutlet weak var WinRate: UILabel!
    @IBOutlet weak var TeamLogo: UIImageView!
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
